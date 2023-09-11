package pro.sky.courseworktelegrambot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.courseworktelegrambot.entities.Pet;
import pro.sky.courseworktelegrambot.entities.State;
import pro.sky.courseworktelegrambot.entities.StateButton;
import pro.sky.courseworktelegrambot.entities.User;
import pro.sky.courseworktelegrambot.repositories.CatRepository;
import pro.sky.courseworktelegrambot.repositories.DogRepository;
import pro.sky.courseworktelegrambot.repositories.StateRepository;
import pro.sky.courseworktelegrambot.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.replace;
//import pro.sky.courseworktelegrambot.config.BotConfig;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    //private final BotConfig botConfig;

    //public TelegramBot(BotConfig botConfig) {
    //    this.botConfig = botConfig;
    //}
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StateRepository stateRepository;


    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private CatRepository catRepository;

    private JpaRepository<? extends Pet, Integer> petRepository(String shelterId) {
        return (shelterId.equals("Dog")) ? dogRepository : catRepository;
    }

    private State initialState;  //начальное состояние для новых пользователей извлечем заранее,
    //остальные будут приходить вместе с пользователем при извлечении его из репозитория
    private State badChoiceState;  //если пришло сообщение, не соответствующее кнопкам
    private State afterShelterChoiceState;  //состояние после выбора приюта.
                              // Нужно, если решим кнопки приютов создавать из табл Shelter
    @PostConstruct
    public void initStates() {
        initialState = stateRepository.findById("Shelter").get();
        badChoiceState = stateRepository.findById("BadChoice").get();
        afterShelterChoiceState = stateRepository.findById("Stage").get();
    }

    private final String returnButtonForTextInput = "Назад к кнопкам";
    //Для состояний ожидания ввода текста создадим заранее клавиатуру. Получилось одной строкой
    private final List<KeyboardRow> keyboardForTextInput =
            Collections.singletonList(new KeyboardRow(
                    Collections.singletonList(new KeyboardButton(
                            returnButtonForTextInput))));

    @Value("${telegram.bot.name}")
    private String botName;

    @Override
    public String getBotUsername() {
        return botName; //botConfig.getBotName();
    }

    @Value("${telegram.bot.token}")
    private String token;

    @Override
    public String getBotToken() {
        return token;//botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) return;
        Message message = update.getMessage();
        long chatId = message.getChatId();
        User user = userRepository.findById(chatId).orElse(null);
        State oldState = null; //старое состояние (или состояние при входе)
        if (user == null) {
            user = new User(chatId, message.getChat().getFirstName(), initialState);
            //oldState останется = null. Это вызовет goToNextState, т.к. oldState<>initialState
            sendMessage(user.getId(), "Привет, " + user.getName(), null, 0);
        } else {
            //запоминаем старое состояние (или состояние при входе)
            oldState = user.getState();
            //Последующие действия возможно назначат новое состояние
            if (oldState.isTextInput()) {
                if (message.getText().equals(returnButtonForTextInput)) {
                    user.setState(user.getPreviousState());
                } else {
                    //хорошо бы сделать рефлексией, т.е. поместить имена методов в табл State
                    switch (user.getState().getId()) {
                        //проверяем, не нужны ли спец действия для определенных состояний
                        case "MessageToVolonteer" -> createMessageToVolonteer(user, message);
                        case "FeedbackRequest" -> createFeedbackRequest(user, message);
                        case "Report" -> acceptReport(user, message);
                        case "AnimalByNumber" -> showAnimal(user, message);
                    }
                }
            } else {
                //проверяем, не нажата ли кнопка. Если нажата, то только установим новое состояние у user
                checkButton(user, message);
            }
        }
        //У usera возможно установлено новое состояние.
        //Если оно изменилось относительно входного, отработаем изменение
        //Здесь посылаем сообщение с кнопками из StateButton
        //Если в новом состоянии кнопок нет, то новое состояние вернем в состояние oldState
        if (!user.getState().equals(oldState)) goToNextState(user, oldState);

        //сохраняем новые состояния пользователя (старое и новое)
        //PreviousState меняем, если к выходу State отличется от входного
        //Т.е. в ожидательных состояниях PreviousState не трогается, пока мы не нажмем "Возврат к боту"
        if (!user.getState().equals(oldState)) user.setPreviousState(oldState);
        user.setStateTime();
        userRepository.save(user);
    }

    /**
     * Отправляет сообщение указанному чату с заданным текстовым сообщением.
     *
     * @param chatId     Идентификатор чата, куда нужно отправить сообщение.
     * @param textToSend Текст сообщения, который следует отправить.
     */
    public void sendMessage(long chatId, String textToSend,
                            ReplyKeyboardMarkup replyKeyboardMarkup, int replyToMessageId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        //при посылке подчеркивания возникает ошибка
        //[400] Bad Request: can't parse entities: Can't find end of the entity starting at byte offset - место подчеркивания
        //поэтому заменяю подчеркивания на тире
        sendMessage.setText(replace(textToSend,"_","-"));
        if (replyKeyboardMarkup != null) {
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        if (replyToMessageId != 0) sendMessage.setReplyToMessageId(replyToMessageId);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            //log.error("Error occurred: " + e.getMessage());
            System.out.println("Error occurred: " + e.getMessage());
        }
    }

    public void sendMessageToUser(User user, String text, int replyToMessageId) {
        //задача - послать юзеру текст, но снабдить его кнопками в соответствии с состоянием
        //этом метод вызываем откуда угодно и любой момент общения с ботом,
        //например, после получения ответа от волонтера
        //если текст в параметре пустой, то используется текст состояния из State
        State state = user.getState();
        if (text==null) text = state.getText();

        //т.е. можно посылать все что угодно и сколько угодно раз с помощью SendMessage,
        //но завершать переписку надо обязательно через sendMessageToUser(user, null, 0)
        //чтобы появились правильные кнопки и обозначилось текущее состояние.
        //Сообщений без кнопок мы посылать не планируем. Какие-нибудь кнопки всегда должны быть.
        //Поэтому надо позаботиться, чтобы в таблицах не оказалось состояний не текстового ввода и без кнопок.

        List<KeyboardRow> keyboard;
        if (state.isTextInput()) {
            keyboard = keyboardForTextInput; //прикрепим только кнопку Назад к кнопкам
        //else if (state.equals(initialState)) {
        //    //Кнопок - Кошки и Собаки - не должно быть в StateButton. Это временнно. Их надо взять из табл Shelter
        } else {
            //если не текстовый ввод и не список приютов,
            //то должны быть кнопки в таблице state_button. Делаем спец клавиатуру
            //для использования переменных в лямбде они должны быть final
            final List<KeyboardRow> customKeyboard = new ArrayList<>();
            final List<StateButton> buttons = state.getButtons();
            //вывести в лог
            if (buttons.isEmpty()) System.out.println("State " + state.getId() + " has no button");
            buttons.stream()
                    .filter(button -> button.getShelterId() == null
                            || button.getShelterId().equals(user.getShelterId()))
                    .mapToInt(StateButton::getRow)
                    .distinct()
                    .forEachOrdered(row -> {
                        KeyboardRow keyboardRow = new KeyboardRow();
                        buttons.stream()
                                .filter(button -> button.getRow() == row
                                        && (button.getShelterId() == null
                                            || button.getShelterId().equals(user.getShelterId())))
                                .sorted(Comparator.comparingInt(StateButton::getCol))
                                .forEach(button -> keyboardRow.add(button.getCaption()));
                        customKeyboard.add(keyboardRow);
                    });
            keyboard = customKeyboard;
        }

        //Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        //устанавливаем список keyboard нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);

        sendMessage(user.getId(), text, replyKeyboardMarkup, 0);
    }


    private void goToInitialState(User user) {
    //Сообщение берем из initialState
    //а кнопки из shelter.name
    //sendMessage(user.getId(), text, replyKeyboardMarkup, 0);
    }

    private void goToNextState(User user, State oldState) {
        //если сменилось состояние
        State state = user.getState(); //новое состояние. Если нет кнопок, то мы его откатим к oldState
        String text = state.getText(); //предстоящее сообщение - текст нового состояния

        if (state.getId().equals("AnimalList")) showAnimalList(user);
        //выясняем, есть ли кнопки в текущем состоянии
        List<StateButton> buttons = state.getButtons();
        if (buttons.isEmpty()  && !state.isTextInput() && !state.equals(initialState)) {
            //если кнопок нет и нет состояния текстового ввода и не список приютов
            //возвращаем состояние назад (к тому, что было при входе в обработку сообщения)
            //этот режим используем для вывода разной информации о приюте, оставаясь в прежнем состоянии
            user.setState(oldState);
            //выводим текст нового и кнопки старого состояния
        }
        sendMessageToUser(user, text, 0);
    }

    private void checkButton(User user, Message message) {
        //для состояний, не являющихся текстовым вводом, т.е. состояний выбора из клавиатуры
        //задача: проверить что пришло (какая кнопка нажата) и установить новое состояние
        if (!message.hasText()) {
            user.setState(badChoiceState);
            return;
        }
        String textFromUser = message.getText();

        if (user.getState().equals(initialState)) {  //если состоялся выбор приюта
            //Это заплатка. По хорошему надо найти по названию ключ из таблицы приютов
            if (!textFromUser.equals("Собаки") & !textFromUser.equals("Кошки")) {
                user.setState(badChoiceState);
                return;
            }
            user.setShelterId((textFromUser.equals("Собаки"))?"Dog":"Cat");
            user.setState(afterShelterChoiceState);
            return;
        }
        List<StateButton> buttons = user.getState().getButtons();
        for (StateButton button : buttons) {
            if (button.getCaption().equals(textFromUser)){
                user.setState(button.getNextState());
                return;
            }
        }
        user.setState(badChoiceState);
    }

    private void createMessageToVolonteer(User user, Message message) {
        if (!message.hasText()) {
            user.setState(badChoiceState);
            return;
        }
        //сохраняем в табл MessageToVolonteer пришедший текст
        //message.getMessageId() - тоже сохраняем
        //чтобы у волонтера была возможность ответить на конкретный вопрос, а не просто послать сообщение
        //состояние не меняем. Пользователь может слать следующие сообщения волонтеру.
        //поэтому потом goToNextState не выполняется и user.setPreviousState тоже не выполняется
    }

    private void createFeedbackRequest(User user, Message message) {
        if (!message.hasText()) {
            user.setState(badChoiceState);
            return;
        }

        //сохраняем в табл FeedbackRequest пришедший текст

        sendMessage(user.getId(), "Запрос обратной связи принят. Волонтер свяжется с вами указанным способом.", null, 0);
        user.setState(user.getPreviousState());
        //состояние изменилось, поэтому вызовется goToNextState
        //и нарисует состояние до входа в запрос обратной связи
    }

    private void acceptReport(User user, Message message) {
        JpaRepository<? extends Pet, Integer> repository = petRepository(user.getShelterId());

        //принимаем отчет из message

        //используем sendMessageToUser, а не sendMessage, чтобы не смахнуть кнопку Возврат к кнопкам
        sendMessageToUser(user, "Принято", 0);
        //состояние не меняем. Пользователь может слать следующие элементы отчета волонтеру.
        //поэтому потом goToNextState не выполняется и user.setPreviousState тоже не выполняется
    }

    private void showAnimalList(User user) {
        String searchTerm; //можно запросить условия отбора, но пока не сделали

        //посылаем картинки из базы. Почти то же, что информация из Shelter

    }

    private void showAnimal(User user, Message message) {
        //id животного спрятано в message
        //посылаем все о животном с помощью sendMessage

        if (!message.hasText()) {
            sendMessageToUser(user, "Ожидаю номер животного:", 0);
            return;
        }
        String text = message.getText();
        //проверить, что число
        int id = Integer.valueOf(text);
        JpaRepository<? extends Pet, Integer> repository = petRepository(user.getShelterId());
        Pet pet = repository.findById(id).orElse(null);
        if (pet == null) {
            sendMessageToUser(user, "Неправильный номер", 0);
        } else {
            sendMessageToUser(user, pet.toString(), 0);
        }
        //В конце используем sendMessageToUser, а не sendMessage, чтобы не смахнуть кнопку Возврат к кнопкам
        sendMessageToUser(user, "Введите следующий номер животного:", 0);
        //состояние не меняем. Пользователь может слать следующие ID животных.
        //поэтому потом goToNextState не выполняется и user.setPreviousState тоже не выполняется
    }

}
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
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.exceptions.UserOrPetIsBusyException;
import pro.sky.courseworktelegrambot.repositories.*;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
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
    private ShelterService shelterService;

    @Autowired
    private FeedbackRequestService feedbackRequestService;
    @Autowired
    private MessageToVolunteerService messageToVolunteerService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private CatRepository catRepository;
    @Autowired
    private DogAdoptionRepository dogAdoptionRepository;
    @Autowired
    private CatAdoptionRepository catAdoptionRepository;
    @Autowired
    private DogReportRepository dogReportRepository;
    @Autowired
    private CatReportRepository catReportRepository;

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
        messageToVolunteerService.telegramBot=this;
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
            try {
                sendMessage(user.getId(), "Привет, " + user.getName(), null, 0);
            } catch (TelegramApiException e) {
                //в логах останется запись
                return;
            }
        } else {
            //запоминаем старое состояние (или состояние при входе)
            oldState = user.getState();
            //Последующие действия возможно назначат новое состояние
            if (oldState.isTextInput()) {
                //в сообщении может не быть текста и message.getText() будет null
                if (returnButtonForTextInput.equals(message.getText())) {
                    user.setState(user.getPreviousState());
                } else {
                    try {
                        //хорошо бы сделать рефлексией, т.е. поместить имена методов в табл State
                        switch (user.getState().getId()) {
                            //проверяем, не нужны ли спец действия для определенных состояний
                            case "MessageToVolonteer" -> createMessageToVolonteer(user, message);
                            case "FeedbackRequest" -> createFeedbackRequest(user, message);
                            case "Report" -> acceptReport(user, message);
                            case "AnimalByNumber" -> showAnimal(user, message);
                        }
                    } catch (TelegramApiException e) {
                        //при невозможности послать ответ, ничего не делаем
                        //в логах останется запись
                        return;
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
        try {
            if (!user.getState().equals(oldState)) goToNextState(user, oldState);
        } catch (TelegramApiException e) {
            //при невозможности послать ответ, ничего не делаем
            //в логах останется запись
            return;
        }

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
     * @param replyKeyboardMarkup если не null, то содержит клавиатуру
     * @param replyToMessageId если не 0, то содержит id предыдущего сообщения,
     *                         в ответ на которое надо отправить заданное
     */
    public void sendMessage (long chatId, String textToSend,
                            ReplyKeyboardMarkup replyKeyboardMarkup, int replyToMessageId)
            throws TelegramApiException {
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
            //log.error("Error occurred: " + e.getMessage());  надо разбираться
            System.out.println("Error occurred: " + e.getMessage());
            throw e;
        }
    }

    //replyToMessageId если не 0, то боту уйдет сообщение в виде - с включенным в ответ вопросом
    public void sendMessageToUser (User user, String text, int replyToMessageId)
        throws TelegramApiException {
        //задача - послать юзеру текст, но снабдить его кнопками в соответствии с состоянием
        //этом метод вызываем откуда угодно и любой момент общения с ботом,
        //например, после получения ответа от волонтераthrows TelegramApiException
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
        //бросает TelegramApiException
        sendMessage(user.getId(), text, replyKeyboardMarkup, replyToMessageId);
     }

    private void goToInitialState(User user) {
    //Сообщение берем из initialState
    //а кнопки из shelter.name - пока не сделано
    //sendMessage(user.getId(), text, replyKeyboardMarkup, 0);
    }

    private void goToNextState(User user, State oldState) throws TelegramApiException {
        //если сменилось состояние
        State state = user.getState(); //новое состояние. Если нет кнопок, то мы его откатим к oldState
        String text = state.getText(); //предстоящее сообщение - текст нового состояния

        //Дальше текст может быть подменен в специальных случаях
        if (state.getId().equals("Report")) {
            text = reportRequestText(user, oldState, null);
        }
        if (text.startsWith("@")) {
            String shelterId = user.getShelterId();
            String informationType = text.substring(1);
            try {
                text = shelterService.getInformation(shelterId, informationType);
            } catch (IllegalAccessException e) {
                //Антон, может быть здесь InformationTypeByShelterNotFound?
                throw new RuntimeException(e);
            }
        }

        //выясняем, есть ли кнопки в текущем состоянии
        List<StateButton> buttons = state.getButtons();
        if (buttons.isEmpty()  && !state.isTextInput() && !state.equals(initialState)) {
            //если кнопок нет и нет состояния текстового ввода и не список приютов
            //возвращаем состояние назад (к тому, что было при входе в обработку сообщения)
            //этот режим используем для вывода разной информации о приюте, оставаясь в прежнем состоянии
            user.setState(oldState);
            //выводим текст нового и кнопки старого состояния
        }
        //бросает TelegramApiException
        sendMessageToUser(user, text, 0);
        if (state.getId().equals("AnimalList")) showAnimalList(user);  //пока ничего не делает
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
        messageToVolunteerService.create(message.getMessageId(), user, message.getText());
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
        feedbackRequestService.save(user, message.getText());

         user.setState(user.getPreviousState());
        //состояние изменилось, поэтому вызовется goToNextState
        //и нарисует состояние до входа в запрос обратной связи
        try {
            sendMessage(user.getId(), "Запрос обратной связи принят. Волонтер свяжется с вами указанным способом.", null, 0);
        } catch (TelegramApiException e) {
            //если не удалось послать подтверждение приема, то ничего страшного.
            //Главное - запрос принят. Ничего не делаем
        }
    }

    private void acceptReport(User user, Message message) {
        //JpaRepository<? extends Pet, Integer> repository = petRepository(user.getShelterId());
        byte[] photo = null; //пока так
        if (message.hasPhoto()) {
            photo = message.getPhoto().get(0).toString().getBytes(); //пока так
        }
        byte[] text = null; //пока так
        if (message.hasDocument()) {
            text = message.getDocument().toString().getBytes(); //пока так
        }

        Report report = reportService.saveReport(user, photo, text);
        //если после сохранения report=null, значит у юзера не было испытательного срока
        //в этом случае reportRequestText вернет его предыдущее состояние
        String requestText = reportRequestText(user, user.getPreviousState(), report);

        //используем sendMessageToUser, а не sendMessage, чтобы не смахнуть кнопку Возврат к кнопкам
        try {
            sendMessageToUser(user, requestText, 0);
        } catch(TelegramApiException e) {
            //если не удалось послать подтверждение приема, то ничего страшного.
            //Главное - отчет принят. Ничего не делаем
        }

        //состояние не меняем. Пользователь может слать следующие элементы отчета волонтеру.
        //поэтому потом goToNextState не выполняется и user.setPreviousState тоже не выполняется
    }

    private void showAnimalList(User user) throws TelegramApiException  {
        //вызывается после вывода сообщения состояния AnimalList - Наши питомцы
        String searchTerm; //можно запросить условия отбора, но пока не сделали
        //посылаем картинки из базы. Пока только toString животных.
        String text = petRepository(user.getShelterId()).findAll().toString();
        sendMessageToUser(user, text, 0);
    }

    private void showAnimal (User user, Message message) throws TelegramApiException {
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

    private String reportRequestText(User user, State oldState, Report report) {
        //oldState - состояние при входе в обработчик сообщений бота
        //Если поиск активного усыновления будет неудачным, то вернемся в него
        //Если report<>null, т.е. отчет сдан до какой-то степени, то oldState будет не востребован

        //Пользователь выбрал, что хочет сдать отчет (report=null)
        //или чего-то прислал в состоянии сдачи отчета (report<>null)
        //что будем ему писать?

        if (report == null) {  //состояние сдачи отчета - неизвестно. Извлечем его из базы
            //сначала проверим активный испытательный срок
            LocalDate date = LocalDate.now();
            String shelterId = user.getShelterId();

            if (shelterId.equals("Dog")) {
                //Ищем у пользователя активный испытательный срок на сегодня
                List<DogAdoption> adoptionList = dogAdoptionRepository.findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        user, date, date);
                if (adoptionList.isEmpty()) {
                    user.setState(oldState);
                    return "В приюте " + shelterId + " у Вас нет активного испытательного срока";
                } else { //Ищем отчет за сегодня
                    //если список усыновлений не пустой, то в нем должна найтись ровно 1 запись
                    DogAdoption adoption = adoptionList.get(0);
                    List<DogReport> reportList = dogReportRepository.findByAdoptionAndDate(adoption, date);
                    if (!reportList.isEmpty()) report = reportList.get(0);
                }
            } else {
                List<CatAdoption> adoptionList = catAdoptionRepository.findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        user, date, date);
                if (adoptionList.isEmpty()) {
                    user.setState(oldState);
                    return "В приюте " + shelterId + " у Вас нет активного испытательного срока";
                } else {
                    CatAdoption adoption = adoptionList.get(0);
                    List<CatReport> reportList = catReportRepository.findByAdoptionAndDate(adoption, date);
                    if (!reportList.isEmpty()) report = reportList.get(0);
                }
            }
        }
        //если отчет в базе не найден, значит он еще не сдан. report будет == null

        //потом узнаем состояние сдачи отчета и сформируем сообщение
        if (report==null || !report.photoIsPresent() && !report.photoIsPresent()) {
            return "Пришлите, пожалуйста, фото (.jpeg) и текстовый отчет (.docx)";
        } else if (!report.photoIsPresent() && report.textIsPresent()){
            return "Осталось прислать фото";
        } else if (report.photoIsPresent() && !report.textIsPresent()) {
            return "Осталось прислать текст";
        } else {
            return "Отчет уже получен. Можете послать еще раз";
        }
    }
}
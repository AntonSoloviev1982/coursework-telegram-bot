package pro.sky.courseworktelegrambot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.courseworktelegrambot.entities.Animal;
import pro.sky.courseworktelegrambot.entities.State;
import pro.sky.courseworktelegrambot.entities.StateButton;
import pro.sky.courseworktelegrambot.entities.User;
import pro.sky.courseworktelegrambot.repositories.CatRepository;
import pro.sky.courseworktelegrambot.repositories.DogRepository;
import pro.sky.courseworktelegrambot.repositories.StateRepository;
import pro.sky.courseworktelegrambot.repositories.UserRepository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private JpaRepository<? extends Animal,Integer> animalRepository(String shelterId) {
        return (shelterId.equals("Dog"))?dogRepository:catRepository;
    }



    private List<State> states;
    private State initialState;
    @PostConstruct
    public void initStates() {
        states = stateRepository.findAll();
        initialState = stateRepository.findById("ShelterChoice").get();
    }

    @Override
    public String getBotUsername() {
        return "Shelters"; //botConfig.getBotName();
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
        User user= userRepository.findById(chatId).orElse(null);
        State oldState = null;
        if (user == null) {
            user = new User(chatId, message.getChat().getFirstName(), initialState);
            //oldState останется = null
        } else {
            //запоминаем старое состояние
            oldState = user.getState();
            user.setPreviousState(oldState);
        }
        //Последующие действия возможно назначат новое состояние
        switch (user.getState().getId()) {
            //проверяем, не нужны ли спец действия для определенных состояний
            case "WaitingMessageToVolonteer"->createMessageToVolonteer(user, message);
            case "WaitingFeedbackRequest"->createFeedbackRequest(user, message);
            case "WaitingReport"->acceptReport(animalRepository(user.getShelterId()),message);
            //выбор приюта

            //проверяем, не нажата ли кнопка. Если нажата, то только установим новое состояние у user
            default -> checkButton(user, message);
        }
        //У usera возможно установлено новое состояние. Если оно изменилось, отработаем изменение
        if (!user.getState().equals(oldState)) goToNextState(user);

        //сохраняем новые состояния пользователя

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
        if (textToSend!=null) sendMessage.setText(textToSend);
        if (replyKeyboardMarkup!=null) {
            sendMessage.enableMarkdown(true);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        if (replyToMessageId!=0) sendMessage.setReplyToMessageId(replyToMessageId);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            //log.error("Error occurred: " + e.getMessage());
            System.out.println("Error occurred: " + e.getMessage());
        }
    }
    private void goToNextState(User user) {
        //если сменилось состояние
        State state = user.getState(); //новое состояние
        //выясняем есть ли кнопки в новом состоянии
        List<StateButton> buttons = state.getButtons();
        if (buttons.isEmpty()) {
            //если нет - только выводим текст и возвращаем состояние назад и выводим кнопки старого состояния
            sendMessage(user.getId(), state.getText(), null,0);
            state = user.getPreviousState();
            user.setState(state);
            buttons = state.getButtons();
        }
        // Создаем клавиатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add("Ко 1");
        keyboardFirstRow.add("Кома 1");
        keyboardFirstRow.add("Команда 2Команда 2Команда 2Команда 2");

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add("К3");
        keyboardSecondRow.add("Команда 4");

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанавливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);

        String text = (state.equals(user.getPreviousState())) ? state.getText() : null;
        sendMessage(user.getId(), text, replyKeyboardMarkup,0);
    }
    private void checkButton(User user, Message message) {
        if (!message.hasText()){
            sendMessage(user.getId(), "Не распознанная команда. Ожидаю текст", null, 0);
            return;
        }
        String textFromUser = message.getText();
        List<StateButton> buttons = user.getState().getButtons();
        for (StateButton button:buttons) {
            if (button.getCaption().equals(textFromUser)) user.setState(button.getNextState());
            return;
        };
        sendMessage(user.getId(), "Не распознанная команда. Ожидаю нажатие кнопки", null, 0);
    }

    private void createMessageToVolonteer(User user, Message question) {
        //question.getText();
        //question.getMessageId();
        //сохранить, чтобы у волонтера была возможность ответить на вопрос, а не просто послать сообщение
    }
    private void createFeedbackRequest(User user, Message contact) {

    }
    private void acceptReport(JpaRepository<? extends Animal,Integer> repository, Message messageFromUser) {

    }
    private void findPets(Repository repository, String searchTerm) {

    }
    private void showPet(Repository repository, int animalId) {

    }

}
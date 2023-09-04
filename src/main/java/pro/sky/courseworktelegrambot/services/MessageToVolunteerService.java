package pro.sky.courseworktelegrambot.services;

import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entity.MessageToVolunteer;
import pro.sky.courseworktelegrambot.exceptions.MessageToVolunteerNotFoundException;
import pro.sky.courseworktelegrambot.repositories.MessageToVolunteerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageToVolunteerService {

    private final TelegramBot telegramBot;

    private final MessageToVolunteerRepository messageToVolunteerRepository;


    public MessageToVolunteerService(TelegramBot telegramBot, MessageToVolunteerRepository messageToVolunteerRepository) {
        this.telegramBot = telegramBot;
        this.messageToVolunteerRepository = messageToVolunteerRepository;
    }

    /**
     * Создает новый объект MessageToVolunteer с вопросом от пользователя, временем когда вопрос был задан
     * и chatId пользователя. Остальные поля остаются пустыми.
     *
     * @param chatId      Идентификатор чата, от куда поступает вопрос.
     * @param question    Текст вопроса.
     */
    public void create(long chatId, String question) {
        MessageToVolunteer messageToVolunteer = new MessageToVolunteer();
        messageToVolunteer.setChatId(chatId);
        messageToVolunteer.setQuestionTime(LocalDateTime.now());
        messageToVolunteer.setQuestion(question);
        messageToVolunteerRepository.save(messageToVolunteer);
    }

    /**
     * Получает список всех объектов MessageToVolunteer из БД, у которых
     * поле answer равно null.
     *
     * @return список объектов MessageToVolunteer.
     */
    public List<MessageToVolunteer> findAllWithoutAnswer() {
        return messageToVolunteerRepository.findAllByAnswerIsNull();
    }

    /**
     * Получает строку с ответом, находит по id нужный объект MessageToVolunteer в БД,
     * присваивает строку полю answer и заполняет поле answerTime.
     *
     * @param id идентификатор объекта MessageToVolunteer.
     * @param answer строка с ответом.
     * @throws MessageToVolunteerNotFoundException если объект не найден.
     */
    public void updateAnswer(int id, String answer) {
        MessageToVolunteer messageToVolunteer = messageToVolunteerRepository.findById(id)
                .orElseThrow(() -> new MessageToVolunteerNotFoundException(id));
        messageToVolunteer.setAnswerTime(LocalDateTime.now());
        messageToVolunteer.setAnswer(answer);
        messageToVolunteerRepository.save(messageToVolunteer);
    }

    /**
     * Отправляет ответ пользователю. Находит нужный объект MessageToVolunteer по id в БД.
     * Добавляет в метод {@link TelegramBot#sendMessage(long, String)} chatId пользователя и ответ.
     * Добавляет время отправки ответа пользователю в поле sentTime.
     *
     * @param id идентификатор объекта MessageToVolunteer.
     * @throws MessageToVolunteerNotFoundException если объект не найден.
     */
    public void sendAnswer(int id) {
        MessageToVolunteer messageToVolunteer = messageToVolunteerRepository.findById(id)
                .orElseThrow(() -> new MessageToVolunteerNotFoundException(id));
        long chatId = messageToVolunteer.getChatId();
        String answer = messageToVolunteer.getAnswer();
        telegramBot.sendMessage(chatId, answer);
        messageToVolunteer.setSentTime(LocalDateTime.now());
        messageToVolunteerRepository.save(messageToVolunteer);
    }
}

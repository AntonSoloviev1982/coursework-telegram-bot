package pro.sky.courseworktelegrambot.services;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.courseworktelegrambot.entities.MessageToVolunteer;
import pro.sky.courseworktelegrambot.entities.User;
import pro.sky.courseworktelegrambot.exceptions.MessageToVolunteerNotFoundException;
import pro.sky.courseworktelegrambot.exceptions.TelegramException;
import pro.sky.courseworktelegrambot.repositories.MessageToVolunteerRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * В класс MessageToVolunteerService находятся методы с бизнес логикой для общения волонтера
 * и пользователя через бот, с помощью вопросов и ответов.
 */
@Service
public class MessageToVolunteerService {
    //вызывает циклическую ссылку private final TelegramBot telegramBot;
    private final MessageToVolunteerRepository messageToVolunteerRepository;


    public MessageToVolunteerService(
            //TelegramBot telegramBot,
            MessageToVolunteerRepository messageToVolunteerRepository) {
        //this.telegramBot = telegramBot;
        this.messageToVolunteerRepository = messageToVolunteerRepository;
    }

    /**
     * Создает новый объект MessageToVolunteer с пользователем, вопросом от пользователя
     * , временем когда вопрос был задан. Остальные поля остаются пустыми.
     *
     * @param user        Юзер, от которого поступает вопрос.
     * @param question    Текст вопроса.
     */
    public void create(User user, String question) {
        MessageToVolunteer messageToVolunteer = new MessageToVolunteer();
        messageToVolunteer.setUser(user);
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
        return List.copyOf(messageToVolunteerRepository.findAllByAnswerIsNull());
    }


    /**
     * Получает строку с ответом, находим по id нужный объект MessageToVolunteer в БД,
     * присваивает строку полю answer и заполняет поле answerTime.
     *
     * @param id идентификатор объекта MessageToVolunteer.
     * @param answer строка с ответом.
     * @param answerToMessage если True, то уйдет сообщение с включенным в ответ вопросом
     * @throws MessageToVolunteerNotFoundException если объект не найден.
     */

    public void updateAnswer (int id, String answer, boolean answerToMessage) {
        MessageToVolunteer messageToVolunteer = messageToVolunteerRepository.findById(id)
                .orElseThrow(() -> new MessageToVolunteerNotFoundException(id));
        messageToVolunteer.setAnswerTime(LocalDateTime.now());
        messageToVolunteer.setAnswer(answer);
        //try {
        //    вызывает циклическую ссылку telegramBot.sendMessageToUser(messageToVolunteer.getUser(), answer, AnswerToMessage ? id : 0);
        //} catch(TelegramApiException e) {
        //    throw new TelegramException();
        //}
        messageToVolunteerRepository.save(messageToVolunteer);
    }

}

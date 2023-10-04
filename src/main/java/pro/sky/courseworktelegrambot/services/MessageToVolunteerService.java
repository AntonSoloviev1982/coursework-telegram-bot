package pro.sky.courseworktelegrambot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
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
    private static final Logger logger = LoggerFactory.getLogger(MessageToVolunteerService.class);

    private final TelegramBotSender telegramBotSender;  //для посылки ответов
    private final MessageToVolunteerRepository messageToVolunteerRepository;


    public MessageToVolunteerService(
            TelegramBotSender telegramBotSender,
            MessageToVolunteerRepository messageToVolunteerRepository) {
        this.telegramBotSender = telegramBotSender;
        this.messageToVolunteerRepository = messageToVolunteerRepository;
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
     *  Cоздает запись в БД в таблице "message_to_volunteer".<br>
     *  Используется метод репозитория {@link JpaRepository#save(Object)}
     *  @param messageId идентификатор сообщения, чтобы у волонтера была возможность
     *                   послать ответ на строго определенное сообщение
     *  @param user пользователь
     *  @param question вопрос к волонтеру
     * */
    public void createMessageToVolunteer(int messageId, User user, String question) {
        MessageToVolunteer messageToVolunteer = new MessageToVolunteer();
        messageToVolunteer.setId(messageId);
        messageToVolunteer.setUser(user);
        messageToVolunteer.setQuestionTime(LocalDateTime.now());
        messageToVolunteer.setQuestion(question);
        messageToVolunteerRepository.save(messageToVolunteer);
    }

    /**
     * Получает строку с ответом, находит по id нужный объект MessageToVolunteer в БД,
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
        try {
            telegramBotSender.sendMessageToUser(messageToVolunteer.getUser(), answer, answerToMessage ? id : 0);
        } catch (TelegramApiException e) {
            logger.error("Ошибка при отправке ответа волонтера "+e.getMessage());
            //TelegramException - это RunTimeException, в отличие от TelegramApiException
            throw new TelegramException(); //при ошибке отметку об отправке ответа (дату ответа) не сохраняем
        }
        messageToVolunteerRepository.save(messageToVolunteer);
    }
}

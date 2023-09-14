package pro.sky.courseworktelegrambot.services;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entities.FeedbackRequest;
import pro.sky.courseworktelegrambot.repositories.FeedbackRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Сервис "запрос обратной связи".
 *  Метод save создает запись в БД в таблице "feedback_request"
 *  с указанием пользователя chatId, временем запроса requestTime и контактными данными contact.
 *  Метод getWaitingList показывает список всех ожидающих вызова пользователей.
 *  Метод executionTimeUpdate заполняет поле executionTime текущим временем после связи с пользователем.
 */

@Service
public class FeedbackRequestService {

    private final FeedbackRequestRepository feedBackRequestRepository;

    public FeedbackRequestService(FeedbackRequestRepository feedBackRequestRepository) {
        this.feedBackRequestRepository = feedBackRequestRepository;
    }

    /**
     *  Cоздает запись в БД в таблице "feedback_request".<br>
     *  Используется метод репозитория {@link JpaRepository#save(Object)}
     *  @param chatId идентификатор пользователя
     *  @param contact контактная информация
     * */
    public void save(Long chatId, String contact) {
        feedBackRequestRepository.save(new FeedbackRequest(chatId, LocalDateTime.now(), contact));
    }

    /**
     *  Находит список всех ожидающих вызова запросов пользователей.
     *  Используется метод репозитория {@link JpaRepository#findAll(Sort)}
     * @return список запросов с нулевым полем executionTime.
     */
    public List<FeedbackRequest> getWaitingList(){
        return feedBackRequestRepository.findAllByExecutionTimeIsNull();
    }

    /**
     *  Изменяет запись в БД в таблице "feedback_request".<br>
     *  Заполняет поле executionTime текущим временем после связи с пользователем.
     *  Используется метод репозитория {@link JpaRepository#save(Object)}
     *  @param id идентификатор запроса.
     *  @throws NoSuchElementException если запроса с таким идентификатором нет в БД.
     */
    public void executionTimeUpdate(Integer id) {
        FeedbackRequest feedBackRequest = feedBackRequestRepository.findById(id).get();
        feedBackRequest.setExecutionTime(LocalDateTime.now());
        feedBackRequestRepository.save(feedBackRequest);
    }

}

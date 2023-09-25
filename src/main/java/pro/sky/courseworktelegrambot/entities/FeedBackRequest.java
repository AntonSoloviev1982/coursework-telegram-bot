package pro.sky.courseworktelegrambot.entities;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Класс FeedBackRequest является родительским классом, дочерние CatAdoption и DogAdoption.
 * Имеет свойства <b>id</b>, <b>userID</b> , <b>petID</b> , <b>date</b> , <b>trialDate</b> , <b>trialDecision</b>

 */
@Entity
@Table(name = "feedback_request")
public class FeedBackRequest {

    /**
     * Уникальное значение, которое присваивается в ходе чата.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * Для каждой беседы между юзером и Ботом  создаем чат-комнату и
     * для ее идентификации генерируем уникальный chatId
     */
    private long chatId;

    /**
     * время запроса
     */
    private LocalDateTime requestTime;

    /**
     *????
     */
    private String contact;

    /**
     * время выполнения
     */
    @Nullable
    private LocalDateTime executionTime;

    /**
     * реализуем репозиторий на основе JPA
     */
    public FeedBackRequest() { //для JPA репозитория
    }

    public FeedBackRequest(Long chatId, LocalDateTime requestTime, String contact) {
        this.chatId = chatId;
        this.requestTime = requestTime;
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public long getChatId() {
        return chatId;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public String getContact() {
        return contact;
    }

    @Nullable
    public LocalDateTime getExecutionTime() {
        return executionTime;
    }

    /**
     * Обновления времени выполнения
     * Используется {@link LocalDateTime}
     * Вместо конкретного объекта в этот метод может быть передан null.
     */
    public void setExecutionTime(@Nullable LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

}

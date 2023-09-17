package pro.sky.courseworktelegrambot.entities;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность "запрос обратной связи".<br>
 *  При запросе пользователя об обратной связи создается запись в базе данных в таблице <u>"feedback_request"</u>
 *  с указанием пользователя <u>chatId</u>, временем запроса <u>requestTime</u> и контактными данными <u>contact</u>.
 *  Поле  <u>executionTime</u> заполняется волонтером после связи с пользователем.
 */

@Entity
@Table(name = "feedback_request")
public class FeedbackRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private long chatId;

    private LocalDateTime requestTime;

    private String contact;

    @Nullable
    private LocalDateTime executionTime;

    public FeedbackRequest() {
    }

    public FeedbackRequest(Long chatId, LocalDateTime requestTime, String contact) {
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

    public void setExecutionTime(@Nullable LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

}

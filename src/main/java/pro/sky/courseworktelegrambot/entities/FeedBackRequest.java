package pro.sky.courseworktelegrambot.entities;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback_request")
public class FeedBackRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private long chatId;

    private LocalDateTime requestTime;

    private String contact;

    @Nullable
    private LocalDateTime executionTime;

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

    public void setExecutionTime(@Nullable LocalDateTime executionTime) {
        this.executionTime = executionTime;
    }

}

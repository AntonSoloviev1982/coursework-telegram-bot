package pro.sky.courseworktelegrambot.entity;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback_request")
public class FeedBackRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private final Long chatId;

    private final LocalDateTime requestTime;

    private final String contact;

    @Nullable
    private LocalDateTime executionTime;

    public FeedBackRequest(Long chatId, LocalDateTime requestTime, String contact) {
        this.chatId = chatId;
        this.requestTime = requestTime;
        this.contact = contact;
    }

    public Long getId() {
        return id;
    }

    public Long getChatId() {
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

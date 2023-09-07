package pro.sky.courseworktelegrambot.entity;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback_request")
public class FeedBackRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime requestTime;

    private String contact;


    @Nullable
    private LocalDateTime executionTime;


    public FeedBackRequest(LocalDateTime requestTime, String contact) {
        this.requestTime = requestTime;
        this.contact = contact;
    }

    public FeedBackRequest() {
    }

    public int getId() {
        return id;
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

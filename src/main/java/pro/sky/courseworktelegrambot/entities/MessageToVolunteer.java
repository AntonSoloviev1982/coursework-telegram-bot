package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_to_volunteer")
public class MessageToVolunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime questionTime;

    private String question;

    private LocalDateTime answerTime;

    private String answer;

    private LocalDateTime sentTime;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getQuestionTime() {
        return questionTime;
    }

    public void setQuestionTime(LocalDateTime questionTime) {
        this.questionTime = questionTime;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public LocalDateTime getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(LocalDateTime answerTime) {
        this.answerTime = answerTime;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getSentTime() {
        return sentTime;
    }

    public void setSentTime(LocalDateTime sentTime) {
        this.sentTime = sentTime;
    }
}

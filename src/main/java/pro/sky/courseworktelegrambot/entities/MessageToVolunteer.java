package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

import java.time.LocalDateTime;

/**
 * Класс MessageToVolunteer
 * Имеет свойства <b>id</b> , <b>chatID</b> , <b>questionTime</b> , <b>question</b> , <b>answerTime</b> <b>answer</b>
 */

@Entity
@Table(name = "message_to_volunteer")
public class MessageToVolunteer {
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
    long chatId;
    /**
     * Отслеживание вопросов по времени
     * Имеет тип {@link LocalDateTime}
     */
    private LocalDateTime questionTime;
    /**
     * Текст вопросов
     */
    private String question;
    /**
     * Отслеживание ответов по времени
     * Имеет тип {@link LocalDateTime}
     */
    private LocalDateTime answerTime;
    /**
     * Текст ответов
     */
    private String answer;

    /**
     * Отслеживание по времени
     */
    private LocalDateTime sentTime;


    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
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

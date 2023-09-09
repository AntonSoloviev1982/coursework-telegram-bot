package pro.sky.courseworktelegrambot.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name="dog_adoption")
public class DogAdoption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int userId; //усыновитель
    private int dogId; //питомец (собака)
    private LocalDateTime date; //дата усыновления
    private LocalDateTime trialDate; //дата окончания испытательного срока
    private int trialDecision; //количество дополнительных дней

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDogId() {
        return dogId;
    }

    public void setDogId(int dogId) {
        this.dogId = dogId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getTrialDate() {
        return trialDate;
    }

    public void setTrialDate(LocalDateTime trialDate) {
        this.trialDate = trialDate;
    }

    public int getTrialDecision() {
        return trialDecision;
    }

    public void setTrialDecision(int trialDecision) {
        this.trialDecision = trialDecision;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DogAdoption that = (DogAdoption) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DogAdoption{" +
                "id=" + id +
                ", userId=" + userId +
                ", dogId=" + dogId +
                ", date=" + date +
                ", trialDate=" + trialDate +
                ", trialDecision=" + trialDecision +
                '}';
    }
}

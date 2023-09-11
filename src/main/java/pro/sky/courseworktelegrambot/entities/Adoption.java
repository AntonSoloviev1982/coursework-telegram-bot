package pro.sky.courseworktelegrambot.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
public abstract class Adoption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private long userId; //усыновитель
    private int petId; //питомец
    private LocalDateTime date; //дата усыновления
    private LocalDateTime trialDate; //дата окончания испытательного срока
    private int trialDecision; //количество дополнительных дней

    public Adoption() {
    }

    public Adoption(int userId, int petId) {
        // !!!требуется проверка!!!
        this.userId = userId;
        this.petId = petId;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
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
        Adoption that = (Adoption) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Adoption{" +
                "id=" + id +
                ", userId=" + userId +
                ", dogId=" + petId +
                ", date=" + date +
                ", trialDate=" + trialDate +
                ", trialDecision=" + trialDecision +
                '}';
    }
}

package pro.sky.courseworktelegrambot.entities;

import java.time.LocalDate;

public class AdoptionDTO {  //для приема параметров создания в теле запроса
    private int id;
    private long userId;
    private int petId;
    private LocalDate trialDate;

    public int getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public int getPetId() {
        return petId;
    }

    public LocalDate getTrialDate() {
        return trialDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public void setTrialDate(LocalDate trialDate) {
        this.trialDate = trialDate;
    }
}

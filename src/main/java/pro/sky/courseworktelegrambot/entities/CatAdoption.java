package pro.sky.courseworktelegrambot.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "cat_adoption")
public class CatAdoption extends Adoption {
    //Питомец. не pet_id, а целый Pet, чтобы возвратить в коллекции описание тоже
    @ManyToOne()
    private Cat pet;

    @Override
    public Cat getPet() {
        return pet;
    }

    public CatAdoption(User user, Cat pet, LocalDate trialDate) {

        super(user, trialDate);
        this.pet = pet;
    }
    public CatAdoption() {
    }
    @Override
    public String toString() {
        return "CatAdoption{" +
                "id=" + super.getId() +
                ", user=" + super.getUser() +
                ", pet=" + pet +
                ", date=" + super.getDate() +
                ", trialDate=" + super.getTrialDate() +
                "}";
    }
}

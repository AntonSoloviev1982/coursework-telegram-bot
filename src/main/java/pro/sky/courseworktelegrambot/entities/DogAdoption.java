package pro.sky.courseworktelegrambot.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "dog_adoption")
public class DogAdoption extends Adoption {
    //Питомец. не pet_id, а целый Pet, чтобы возвратить в коллекции усыновлений и описание питомца тоже
    @ManyToOne
    private Dog pet;
    @Override
    public Dog getPet() {
        return pet;
    }
    public DogAdoption(User user, Dog pet, LocalDate trialDate) {
        super(user, trialDate);
        this.pet = pet;
    }
    public DogAdoption() {
    }
    @Override
    public String toString() {
        return "DogAdoption{" +
                "id=" + super.getId() +
                ", user=" + super.getUser() +
                ", pet=" + pet +
                ", date=" + super.getDate() +
                ", trialDate=" + super.getTrialDate() +
                "}";
    }

}

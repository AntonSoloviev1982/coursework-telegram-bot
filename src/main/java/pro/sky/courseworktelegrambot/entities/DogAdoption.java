package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

@Entity
@Table(name = "dog_adoption")
public class DogAdoption extends Adoption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public DogAdoption() {
    }

    public DogAdoption(int userId, int dogId) {
        super(userId, dogId);
    }
}

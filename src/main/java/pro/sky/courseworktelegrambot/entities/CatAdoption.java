package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

@Entity
@Table(name = "cat_adoption")
public class CatAdoption extends Adoption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public CatAdoption() {
    }

    public CatAdoption(int userId, int catId) {
        super(userId, catId);
    }
}

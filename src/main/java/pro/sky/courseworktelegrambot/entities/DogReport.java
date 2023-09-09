package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

@Entity
@Table(name = "dog_report")
public class DogReport extends Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

}

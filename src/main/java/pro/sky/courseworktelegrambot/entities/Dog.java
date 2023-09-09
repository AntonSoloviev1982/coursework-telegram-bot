package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

/**
 * Object Dog
 */
@Entity
@Table(name = "dog")
public class Dog extends Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    public Dog(){}

    public Dog(String name, String breed, int age, byte[] photo) {
        super(name, breed, age, photo);
    }
}

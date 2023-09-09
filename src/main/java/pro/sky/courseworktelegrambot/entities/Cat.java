package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

/**
 * Object Cat
 */

@Entity
@Table(name = "cat")
public class Cat extends Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    public Cat(){}

    public Cat(String name, String breed, int age, byte[] photo) {
        super(name, breed, age, photo);
    }
}

package pro.sky.courseworktelegrambot.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cat extends Animal{
    @Id
    int Id;
}

package pro.sky.courseworktelegrambot.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
@Entity
public class Dog extends Animal{
    @Id
    int Id;
}

package pro.sky.courseworktelegrambot.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Object Cat
 */

@Entity
@Table(name = "cat")
public class Cat extends Pet {
    public Cat() {
    }

    public Cat(Pet pet) {
        setId(pet.getId());
        setName(pet.getName());
        setBreed(pet.getBreed());
        setAge(pet.getAge());
        setPhoto(pet.getPhoto());
        setAdopted(pet.isAdopted());
    }
}

package pro.sky.courseworktelegrambot.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Object Dog
 */
@Entity
@Table(name = "dog")
public class Dog extends Pet {
    public Dog() {
    }

    public Dog(Pet pet) {
        setId(pet.getId());
        setName(pet.getName());
        setBreed(pet.getBreed());
        setAge(pet.getAge());
        setPhoto(pet.getPhoto());
        setAdopted(pet.isAdopted());
    }
}

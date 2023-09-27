package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * Object Pet
 */
//Класс не абстрактный.
//Объекты этого класса Spring будет собирать из тела запросов на создание и обновление
@MappedSuperclass
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String breed;
    /**
     * Возраст животного
     */
    private int age;
    @Lob
    private byte[] photo;
    private boolean isAdopted;

    //конструкторы не нужны, создавать будет Spring из запросов

    public int getId() {
        return id;
    }
    public void setId(int id) { //для преобразования из Pet в Dog или Cat
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public boolean getHasPhoto() {
        return photo != null;
    }

    public boolean isAdopted() {
        return isAdopted;
    }

    public void setAdopted(boolean adopted) {
        this.isAdopted = adopted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return id == pet.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", breed='" + breed + '\'' +
                ", age=" + age +
                ", isAdopted=" + isAdopted +
                '}';
    }
}

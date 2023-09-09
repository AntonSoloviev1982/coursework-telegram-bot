package pro.sky.courseworktelegrambot.entities;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Object dog
 */
@Entity
@Table(name = "dog")
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String breed;
    private int age;
    @Lob
    private byte[] photo;
    private boolean isAdopted;

    public Dog() {
    }

    public Dog(String name, String breed, int age, byte[] photo) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.photo = photo;
        this.isAdopted = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean isAdopted() {
        return isAdopted;
    }

    public void setAdopted(boolean adopted) {
        isAdopted = adopted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dog dog = (Dog) o;
        return id == dog.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "id=" + id +
                "Dog " + "name=" + name + '\'' +
                ", breed='" + breed + '\'' +
                ", age=" + age + '\'' +
                ", isAdopted=" + isAdopted;
    }
}

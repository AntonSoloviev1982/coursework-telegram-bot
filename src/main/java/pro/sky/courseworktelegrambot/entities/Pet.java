package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * Object Pet
 */
public abstract class Pet {
    private int id;
    private String name;
    private String breed;
    private int age;
    @Lob
    private byte[] photo;
    private boolean isAdopted;
    public Pet(){}

    public Pet(String name, String breed, int age, byte[] photo) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.photo = photo;
        this.isAdopted = false;
    }

    public int getId() {
        return id;
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
                ", photo=" + Arrays.toString(photo) +
                ", isAdopted=" + isAdopted +
                '}';
    }
}

package pro.sky.courseworktelegrambot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "cat")
public class Cat {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Getter
    private String name;
    @Getter
    private String breed;
    @Getter
    private int age;
    private boolean adopted;
    @Getter
    private byte[] photo;

    public Cat(long id, String name, String breed, int age, boolean adopted, byte[] photo) {
        this.name = name;
        this.breed = breed;
        this.age = age;
        this.adopted = adopted;
        this.photo = photo;
    }

    public Cat() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isAdopted() {
        return adopted;
    }

    public void setAdopted(boolean adopted) {
        adopted = adopted;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cat cat)) return false;
        return getId() == cat.getId() && getAge() == cat.getAge() && isAdopted() == cat.isAdopted() && Objects.equals(getName(), cat.getName()) && Objects.equals(getBreed(), cat.getBreed()) && Arrays.equals(getPhoto(), cat.getPhoto());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getName(), getBreed(), getAge(), isAdopted());
        result = 31 * result + Arrays.hashCode(getPhoto());
        return result;
    }

}

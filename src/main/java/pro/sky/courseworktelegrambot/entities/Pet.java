package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Objects;

/**
 * Класс Pet является родительским классом, дочерние Cat и Dog.
 * Имеет наследуемые свойства <b>id</b>, <b>name</b> , <b>breed</b> , <b>age</b> , <b>photo</b> , <b>isAdopted</b>
 */

@MappedSuperclass
public abstract class Pet {

    /**
     * Уникальный значение, которое присваивается Cat либо Dog.
     * Этот номер используется,
     * чтобы идентифицировать объект и осуществлять доступ к его свойствам и методам.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Поле имени животного
     */
    private String name;

    /**
     * Порода животного
     */
    private String breed;

    /**
     * Возраст животного
     */
    private int age;
    /**
     * Фото животного
     */
    @Lob
    private byte[] photo;

    /**
     * Поле статуса усыновления
     */
    private boolean isAdopted;

    public Pet() {
    }

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

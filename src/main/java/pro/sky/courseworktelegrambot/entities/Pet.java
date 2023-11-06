package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.util.Objects;

/**
 * Базовый класс для сущностей, представляющих домашних животных.
 */
@MappedSuperclass
public class Pet {    
//Класс не абстрактный.
//Объекты этого класса Spring будет собирать из тела запросов на создание и обновление 
//и потом из них пересоздавать объекты класса Dog или Pet
    /**
     * Идентификатор животного.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * Имя животного.
     */
    private String name;
    /**
     * Порода животного.
     */
    private String breed;
    /**
     * Возраст животного.
     */
    private int age;
    /**
     * Фотография животного.
     */
    @Lob
    @Column(columnDefinition = "oid")
    private byte[] photo;
    /**
     * Статус усыновления животного.
     */
    private boolean adopted;
    
    //конструкторы не нужны, создавать будет Spring из запросов
    
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

    /**
     * Возвращает true, если у животного есть фотография.
     *
     * @return true, если фотография есть; false в противном случае
     */
    public boolean getHasPhoto() {
        return photo != null;
    }

    public boolean isAdopted() {
        return adopted;
    }

    public void setAdopted(boolean adopted) {
        this.adopted = adopted;
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
                ", isAdopted=" + adopted +
                '}';
    }
}

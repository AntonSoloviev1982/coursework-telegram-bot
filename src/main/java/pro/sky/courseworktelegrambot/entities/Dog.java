package pro.sky.courseworktelegrambot.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Класс Dog является дочерним классом, наследуеться от класса Pet.
 * Имеет свойства <b>name</b> , <b>breed</b> , <b>age</b> , <b>photo</b> , <b>isAdopted</b>
 */
@Entity
@Table(name = "dog")
public class Dog extends Pet {

}

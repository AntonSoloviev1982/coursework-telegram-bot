package pro.sky.courseworktelegrambot.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Класс Cat является дочерним классом, наследуеться от класса Pet.
 * Имеет наследуемые свойства <b>name</b> , <b>breed</b> , <b>age</b> , <b>photo</b> , <b>isAdopted</b>
 */

@Entity
@Table(name = "cat")
public class Cat extends Pet {

}

package pro.sky.courseworktelegrambot.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Класс CatAdoption является дочерним классом, наследуеться от класса Adoption.
 * Имеет наследуемые свойства <b>id</b>, <b>userID</b> , <b>petID</b> , <b>date</b> , <b>trialDate</b> , <b>trialDecision</b>
 */
@Entity
@Table(name = "cat_adoption")
public class CatAdoption extends Adoption {
}


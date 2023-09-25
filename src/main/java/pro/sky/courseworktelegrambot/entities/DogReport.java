package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

/**
 * Класс DogReport является дочерним классом, наследуется от класса Report,
 * Имеет наследуемые свойства <b>id</b>, <b>adoptionID</b> , <b reportDate</b> , <b>photo</b> , <b>text</b> >
 */
@Entity
@Table(name = "dog_report")
public class DogReport extends Report {
}

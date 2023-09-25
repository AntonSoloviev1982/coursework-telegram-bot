package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * Класс CatReport является дочерним классом, наследуется от класса Report,
 * Имеет наследуемые свойства <b>id</b>, <b>adoptionID</b> , <b reportDate</b> , <b>photo</b> , <b>text</b> >
 */
@Entity
@Table(name = "cat_report")
public class CatReport extends Report {
}


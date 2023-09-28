package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

//Объекты этого класса никогда не будут созданы.
//Он будет унаследован потомками DogAdoption и CatAdoption
//нужен, чтобы создать две одинаковые таблицы dog_adoption и cat_аdoption
//и обращаться к ним через универсальный репозиторий типа JpaRepository<? extends Adoption, Integer>
//Две таблицы возникают из ТЗ данного нам заказчиком. Мы переспрашивали у наставника.
//На самом деле иметь 2 одинаковые таблицы - плохое решение

@MappedSuperclass
public abstract class Adoption {
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO) так выдает Schema-validation: missing sequence [hibernate_sequence]
    @GeneratedValue(strategy = GenerationType.IDENTITY) //так работает, но поле id д.б. с AUTO_INCREMENT
    //а вот так тоже работает, если в базе есть последовательность my_sequence
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_sequence")
    //@SequenceGenerator(name="my_sequence", sequenceName="my_sequence", строго allocationSize=1)
    private int id;
    //Усыновитель. не user_id, а целый User, чтобы возвратить в коллекции усыновлений описание усыновителя тоже
    @ManyToOne
    private User user;

    //Дата усыновления. За один день один пользователь может усыновить только одно животное.
    //Да и для усыновлений за разные дни нельзя допускать пересечение испытательных сроков
    private LocalDate date;
    //Дата окончания испытательного срока. Назначается волонтером.
    //В интервале от date до trialDate бот ждет отчеты
    //При отборе животного волонтер устанавливает trialDate в 01.01.2001
    private LocalDate trialDate;

    public Adoption() {
    }

    public Adoption(User user, LocalDate trialDate) {
        this.user = user;
        this.date = LocalDate.now();
        this.trialDate = trialDate;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    } //для тестов

    public User getUser() {
        return user;
    }

    //не удалось включить сюда Pet, т.к. здесь нельзя поставить связь
    //а возвращать список усыновлений волонтеру хочется вместе с описанием животных
    //пришлось поле pet унести в наследников
    public abstract Pet getPet();

    public LocalDate getDate() {
        return date;
    }


    public LocalDate getTrialDate() {
        return trialDate;
    }

    public void setTrialDate(LocalDate trialDate) {
        this.trialDate = trialDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adoption that = (Adoption) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Adoption{" +
                "id=" + id +
                ", user=" + user +
                //", pet=" + pet +
                ", date=" + date +
                ", trialDate=" + trialDate +
                "}";
    }
}

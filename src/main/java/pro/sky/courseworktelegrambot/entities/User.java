package pro.sky.courseworktelegrambot.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    private long id; //id чата

    private String name;
    private String shelterId;
    @ManyToOne
    private State state;
    @ManyToOne
    private State previousState;
    private LocalDateTime stateTime;

    public User(long id, String name, State state) {  //для создания нового
        this.id = id;
        this.name = name;
        this.state = state;
    }
    public User() {  //для JPA репозитория
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getPreviousState() {
        return previousState;
    }

    public void setPreviousState(State previousState) {
        this.previousState = previousState;
    }

    public String getShelterId() {
        return shelterId;
    }

    public void setShelterId(String shelterId) {
        this.shelterId = shelterId;
    }

    public LocalDateTime getStateTime() {
        return stateTime;
    }

    public void setStateTime() {
        this.stateTime = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

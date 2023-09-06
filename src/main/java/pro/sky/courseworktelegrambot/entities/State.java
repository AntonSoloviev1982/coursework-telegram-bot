package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class State {
    @Id
    private String id;
    private String text;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="state_id")
    private List<StateButton> buttons;
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
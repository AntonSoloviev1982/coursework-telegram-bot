package pro.sky.courseworktelegrambot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class State {
    @Id
    private String id;
    private String text;

    @OneToMany
    @JoinColumn(name="state_id")
    private List<StateButton> buttons;
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
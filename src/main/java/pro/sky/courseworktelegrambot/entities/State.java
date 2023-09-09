package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class State {
    @Id
    private String id;
    private String text;
    private Boolean textInput;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State state)) return false;

        return id.equals(state.id);
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name="state_id")
    private List<StateButton> buttons;
    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }
    public Boolean isTextInput() {
        return textInput;
    }
    public List<StateButton>getButtons() {
        return buttons;
    }

}
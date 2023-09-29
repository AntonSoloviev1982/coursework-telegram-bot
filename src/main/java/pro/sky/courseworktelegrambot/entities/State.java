package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.util.List;

@Entity
public class State {
    @Id
    private String id;
    //сообщение, которое отправляется пользователю при переходе в это состояние
    private String text;
    // Если true, то ждем произвольный текст. Иначе ждем только текст от нажатых кнопок
    private Boolean textInput;  //состояние текстового ввода.
    //некоторые состояния имеют спец имена и соответствуют перечислению NamedState
    @Enumerated(EnumType.STRING)
    private NamedState namedState;
    //кнопки подтягиваем сразу
    @OneToMany(fetch = FetchType.EAGER)  //по умолчанию (fetch = FetchType.LAZY)
    @JoinColumn(name="state_id")
    private List<StateButton> buttons;

    //для тестов
    public State(String id, String text, Boolean textInput, NamedState namedState, List<StateButton> buttons) {
        this.id = id;
        this.text = text;
        this.textInput = textInput;
        this.namedState = namedState;
        this.buttons = buttons;
    }

    public State() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State state)) return false;

        return id.equals(state.id);
    }

    public String getId() {
        return id;
    }
    public String getText() {
        return text;
    }
    public Boolean isTextInput() {
        return textInput;
    }
    public NamedState getNamedState() {
        return namedState;
    }
    public List<StateButton>getButtons() {
        return buttons;
    }

}
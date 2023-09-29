package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

@Entity
@IdClass(StateButtonPK.class)
public class StateButton {
    @Id
    private String state_id; //в работе не потребуется. Поле существует, только для hibernate
    @Id
    private String caption;
    @ManyToOne   //по умолчанию (fetch = FetchType.EAGER)
    private State nextState;
    @Column(name="button_row") //имя колонки row запрещено в базе
    private byte row;
    @Column(name="button_col")
    private byte col;
    @Enumerated(EnumType.STRING)
    private ShelterId shelterId;  //если пусто, то для любого приюта. если заполнено, то кнопка только для указанного

    public StateButton() {
    }

    //для тестов
    public StateButton(String state_id, String caption, State nextState, byte row, byte col, ShelterId shelterId) {
        this.state_id = state_id;
        this.caption = caption;
        this.nextState = nextState;
        this.row = row;
        this.col = col;
        this.shelterId = shelterId;
    }

    public String getState_id() {  //думаю, никогда не потребуется
        return state_id;
    }

    public String getCaption() {
        return caption;
    }

    public State getNextState() {
        return nextState;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public ShelterId getShelterId() {
        return shelterId;
    }
}


package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

@Entity
@IdClass(StateButtonPK.class)
public class StateButton {
    @Id
    private String state_id; //в работе не потребуется. Поле существует, только для hibernate
    @Id
    private String caption;
    @ManyToOne   //(fetch = FetchType.LAZY)
    private State nextState;
    @Column(name="button_row") //имя колонки row запрещено в базе
    private byte row;
    @Column(name="button_col")
    private byte col;
    private String shelterId;  //если пусто, то для любого приюта. если заполнено, то кнопка только для указанного

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

    public String getShelterId() {
        return shelterId;
    }
}


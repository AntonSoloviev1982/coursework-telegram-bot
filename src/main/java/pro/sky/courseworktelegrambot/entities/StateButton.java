package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;

@Entity
@IdClass(StateButtonPK.class)
public class StateButton {
    @Id
    private String state_id;
    @Id
    private String caption;
    @ManyToOne   //(fetch = FetchType.LAZY)
    private State nextState;


    public String getState_id() {  //думаю, никогда не потребуется
        return state_id;
    }

    public String getCaption() {
        return caption;
    }

    public State getNextState() {
        return nextState;
    }
}


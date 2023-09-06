package pro.sky.courseworktelegrambot.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(StateButtonPK.class)
public class StateButton {
    @Id
    private String state_id;
    @Id
    private String caption;
    private String next_state_id;
}


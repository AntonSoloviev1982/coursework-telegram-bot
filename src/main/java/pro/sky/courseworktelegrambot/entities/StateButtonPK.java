package pro.sky.courseworktelegrambot.entities;

import java.io.Serializable;

public class StateButtonPK implements Serializable {
        private String state_id;
        private String caption;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StateButtonPK that)) return false;

        if (!state_id.equals(that.state_id)) return false;
        return caption.equals(that.caption);
    }

    @Override
    public int hashCode() {
        int result = state_id.hashCode();
        result = 31 * result + caption.hashCode();
        return result;
    }
}

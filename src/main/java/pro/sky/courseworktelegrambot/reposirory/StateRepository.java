package pro.sky.courseworktelegrambot.reposirory;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.courseworktelegrambot.entity.State;

public interface StateRepository extends JpaRepository<State, String> {

}

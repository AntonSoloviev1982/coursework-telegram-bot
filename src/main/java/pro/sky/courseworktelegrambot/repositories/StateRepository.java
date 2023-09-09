package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.courseworktelegrambot.entities.State;

public interface StateRepository extends JpaRepository<State, String> {
//мы используем StateRepository только для получения начального состояния для нового пользователя
}

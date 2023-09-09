package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.courseworktelegrambot.entities.State;
import pro.sky.courseworktelegrambot.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}

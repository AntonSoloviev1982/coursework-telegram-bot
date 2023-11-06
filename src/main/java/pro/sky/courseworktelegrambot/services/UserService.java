package pro.sky.courseworktelegrambot.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entities.User;
import pro.sky.courseworktelegrambot.repositories.UserRepository;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Метод возвращает всех пользователей
     * Используется метод репозитория {@link JpaRepository#findAll()}
     * @return  Список всех пользователей
     * */
    public List<User> getAllUsers() {
        return List.copyOf(userRepository.findAll());
    }
}

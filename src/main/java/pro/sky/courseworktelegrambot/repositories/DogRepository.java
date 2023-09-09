package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.courseworktelegrambot.entities.Dog;
@Repository
public interface DogRepository  extends JpaRepository<Dog, Integer> {
}

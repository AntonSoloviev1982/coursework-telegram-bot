package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.courseworktelegrambot.entities.DogAdoption;

import java.util.List;

@Repository
public interface DogAdoptionRepository extends JpaRepository<DogAdoption,Integer> {
    boolean existsByUserIdOrPetId(Integer userId, Integer petId);
}

package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.courseworktelegrambot.entity.DogAdoption;
@Repository
public interface DogAdoptionRepository extends JpaRepository<DogAdoption,Integer> {
}

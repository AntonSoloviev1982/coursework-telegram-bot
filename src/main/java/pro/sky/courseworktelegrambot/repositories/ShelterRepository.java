package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.courseworktelegrambot.entity.Shelter;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Integer> {

}

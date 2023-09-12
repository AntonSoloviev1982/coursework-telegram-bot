package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.courseworktelegrambot.entities.Shelter;

import java.util.Optional;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter, Integer> {

    Shelter findById(String id);

    Shelter deleteById(String id);

}

package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.courseworktelegrambot.entities.CatAdoption;
import pro.sky.courseworktelegrambot.entities.DogAdoption;
import pro.sky.courseworktelegrambot.entities.Pet;
import pro.sky.courseworktelegrambot.entities.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CatAdoptionRepository extends JpaRepository<CatAdoption,Integer> {
    List<CatAdoption> findByUserAndPetAndDateLessThanEqualAndTrialDateGreaterThanEqual(
            //trialDate_parameter >= date_base AND date_parameter <= trialDate_base
            User user, Pet pet, LocalDate trialDate, LocalDate Date);
}

package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.courseworktelegrambot.entities.DogAdoption;
import pro.sky.courseworktelegrambot.entities.Pet;
import pro.sky.courseworktelegrambot.entities.User;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DogAdoptionRepository extends JpaRepository<DogAdoption,Integer> {
    //поиск усыновлений пользователя с пересекающимся испытательным сроком,
    List<DogAdoption> findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
            //trialDate_parameter >= date_base AND date_parameter <= trialDate_base
            User user, LocalDate trialDate, LocalDate Date);
    //поиск усыновлений животного с пересекающимся испытательным сроком,
    List<DogAdoption> findByPetAndDateLessThanEqualAndTrialDateGreaterThanEqual(
            //trialDate_parameter >= date_base AND date_parameter <= trialDate_base
            Pet pet, LocalDate trialDate, LocalDate Date);
    //поиск активных, действующих на дату усыновлений
    List<DogAdoption> findByDateLessThanEqualAndTrialDateGreaterThanEqual(
            //date_parameter >= date_base AND date_parameter <= trialDate_base
            LocalDate date1, LocalDate date2);
}

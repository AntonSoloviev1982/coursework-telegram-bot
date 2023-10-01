package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pro.sky.courseworktelegrambot.entities.DogAdoption;
import pro.sky.courseworktelegrambot.entities.DogReport;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DogReportRepository extends JpaRepository<DogReport, Integer> {
    //для волонтера
    List<DogReport> findByDate(LocalDate date);
    //для бота для оценки состояния сдачи отчета
    List<DogReport> findByAdoptionAndDate(DogAdoption dogAdoption, LocalDate date);

    List<DogReport> findByDateAndPhotoIsNotNullAndTextIsNotNull(LocalDate date);

    @Query(value = "SELECT * FROM dog_report where adoption =: 1 " +
            "and photo is not null " +
            "and text is not null " +
            "order by date desc limit 1", nativeQuery = true)
    DogReport findLatestReport(DogAdoption dogAdoption);

}

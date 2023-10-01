package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.courseworktelegrambot.entities.CatAdoption;
import pro.sky.courseworktelegrambot.entities.CatReport;

import java.time.LocalDate;
import java.util.List;

public interface CatReportRepository extends JpaRepository<CatReport, Integer> {
    List<CatReport> findByDate(LocalDate date);
    //для бота для оценки состояния сдачи отчета
    List<CatReport> findByAdoptionAndDate(CatAdoption catAdoption, LocalDate date);

    List<CatReport> findByDateAndPhotoIsNotNullAndTextIsNotNull(LocalDate date);

    @Query(value = "SELECT * FROM cat_report where adoption =: 1 " +
            "and photo is not null " +
            "and text is not null " +
            "order by date desc limit 1", nativeQuery = true)
    CatReport findLatestReport(CatAdoption catAdoption);
}

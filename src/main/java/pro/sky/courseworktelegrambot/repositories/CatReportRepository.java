package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.courseworktelegrambot.entities.CatAdoption;
import pro.sky.courseworktelegrambot.entities.CatReport;
import pro.sky.courseworktelegrambot.entities.DogAdoption;
import pro.sky.courseworktelegrambot.entities.DogReport;

import java.time.LocalDate;
import java.util.List;

public interface CatReportRepository extends JpaRepository<CatReport, Integer> {
    List<CatReport> findByDate(LocalDate date);
    //для бота для оценки состояния сдачи отчета
    List<CatReport> findByAdoptionAndDate(CatAdoption catAdoption, LocalDate date);
}

package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.courseworktelegrambot.entities.CatReport;

import java.time.LocalDate;
import java.util.List;

public interface CatReportRepository extends JpaRepository<CatReport, Long> {

    List<CatReport> findAllById(Long id);

    List<CatReport> findAllByReportDate(LocalDate localDate);

}

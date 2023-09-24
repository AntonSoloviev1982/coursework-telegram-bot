package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.courseworktelegrambot.entities.CatReport;
import java.time.LocalDate;
import java.util.List;

public interface CatReportRepository extends JpaRepository<CatReport, Integer> {

    //List<CatReport> findAllById(Long id);

    List<CatReport> findAllByDate(LocalDate date);

}

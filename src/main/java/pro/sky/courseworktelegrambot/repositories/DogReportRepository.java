package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.courseworktelegrambot.entities.DogReport;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DogReportRepository extends JpaRepository<DogReport,Integer> {
    List<DogReport> findAllByReportDate(LocalDate reportDate);
}

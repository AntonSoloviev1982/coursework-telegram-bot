package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.courseworktelegrambot.entities.FeedbackRequest;

import java.util.List;

public interface FeedbackRequestRepository extends JpaRepository<FeedbackRequest, Integer> {

    List<FeedbackRequest> findAllByExecutionTimeIsNull();

}

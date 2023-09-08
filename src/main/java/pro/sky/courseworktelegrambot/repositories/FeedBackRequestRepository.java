package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.courseworktelegrambot.entity.FeedBackRequest;

import java.util.List;

public interface FeedBackRequestRepository extends JpaRepository<FeedBackRequest, Long> {

    List<FeedBackRequest> findAllByExecutionTimeIsNull();

}

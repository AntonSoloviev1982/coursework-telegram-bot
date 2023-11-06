package pro.sky.courseworktelegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.courseworktelegrambot.entities.MessageToVolunteer;

import java.util.List;

@Repository
public interface MessageToVolunteerRepository extends JpaRepository<MessageToVolunteer, Integer> {
    List<MessageToVolunteer> findAllByAnswerIsNull();

}

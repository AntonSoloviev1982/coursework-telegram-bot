package pro.sky.courseworktelegrambot.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.courseworktelegrambot.entities.FeedBackRequest;
import pro.sky.courseworktelegrambot.repositories.FeedBackRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedBackRequestService {

    private final FeedBackRequestRepository feedBackRequestRepository;

    public FeedBackRequestService(FeedBackRequestRepository feedBackRequestRepository) {
        this.feedBackRequestRepository = feedBackRequestRepository;
    }

    @Transactional
    public void save(Long chatId, String text) {
        feedBackRequestRepository.save(new FeedBackRequest(chatId, LocalDateTime.now(), text));
    }

    public List<FeedBackRequest> getWaitingList(){
        return feedBackRequestRepository.findAllByExecutionTimeIsNull();
    }

    @Transactional
    public void executionTimeUpdate(Long id) {
        FeedBackRequest feedBackRequest = feedBackRequestRepository.findById(id).get();
        feedBackRequest.setExecutionTime(LocalDateTime.now());
        feedBackRequestRepository.save(feedBackRequest);
    }

}

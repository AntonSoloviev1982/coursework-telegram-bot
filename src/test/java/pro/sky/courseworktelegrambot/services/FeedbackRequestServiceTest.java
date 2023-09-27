package pro.sky.courseworktelegrambot.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.courseworktelegrambot.entities.FeedbackRequest;
import pro.sky.courseworktelegrambot.entities.User;
import pro.sky.courseworktelegrambot.repositories.FeedbackRequestRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackRequestServiceTest {

    @Mock
    private FeedbackRequestRepository feedbackRequestRepository;

    @InjectMocks
    private FeedbackRequestService feedbackRequestService;

    @Test
    void createFeedbackRequestTest() {
        User user1 = new User();
        String contact1 = "contact1";
        feedbackRequestService.createFeedbackRequest(user1, contact1);

        verify(feedbackRequestRepository,only()).save(any());
    }

    @Test
    void getFeedbackRequestTest() {
        User user2 = new User();
        String contact2 = "contact2";
        FeedbackRequest feedbackRequest2 = new FeedbackRequest(user2, LocalDateTime.now(), contact2);

        when(feedbackRequestRepository.findById(any())).thenReturn(Optional.of(feedbackRequest2));
        Assertions.assertTrue(feedbackRequest2.equals(feedbackRequestService.getFeedbackRequest(2)));
        verify(feedbackRequestRepository,only()).findById(any());

        when(feedbackRequestRepository.findById(any())).thenReturn(Optional.empty());
        Assertions.assertThrows(NoSuchElementException.class, () -> feedbackRequestService.getFeedbackRequest(3));
        verify(feedbackRequestRepository,times(2)).findById(any());
    }

}
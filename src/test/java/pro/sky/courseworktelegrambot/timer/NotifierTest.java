package pro.sky.courseworktelegrambot.timer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.repositories.CatAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.CatReportRepository;
import pro.sky.courseworktelegrambot.repositories.DogAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.DogReportRepository;
import pro.sky.courseworktelegrambot.services.MessageToVolunteerService;
import pro.sky.courseworktelegrambot.services.TelegramBotSender;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotifierTest {

    @Mock
    private  CatAdoptionRepository catAdoptionRepository;
    @Mock
    private  DogAdoptionRepository dogAdoptionRepository;
    @Mock
    private  CatReportRepository catReportRepository;
    @Mock
    private  DogReportRepository dogReportRepository;
    @Mock
    private MessageToVolunteerService messageToVolunteerService;
    @Mock
    private  TelegramBotSender telegramBotSender;

    @InjectMocks
    private Notifier notifier;

    @Test
    void sendWarningNoCatReportTest() throws TelegramApiException {
        Cat cat7 = new Cat();
        Cat cat8 = new Cat();
        Cat cat9 = new Cat();
        cat7.setId(7);
        cat8.setId(8);
        cat8.setId(9);
        User user7 = new User(); //прислал последний отчет 3 дня назад
        User user8 = new User(); //прислал последний отчет вчера
        User user9 = new User(); //прислал последний отчет сегодня
        user7.setId(77);
        user8.setId(88);
        user9.setId(99);
        CatAdoption adoption7 = new CatAdoption(user7, cat7, LocalDate.now());
        adoption7.setId(777);
        CatAdoption adoption8 = new CatAdoption(user8, cat8, LocalDate.of(2023, 11, 5));
        adoption8.setId(888);
        CatAdoption adoption9 = new CatAdoption(user9, cat9, LocalDate.of(2023, 11, 5));
        adoption8.setId(999);
        CatReport report7 = new CatReport(
                adoption7,LocalDate.now().plusDays(-3), new byte[]{1,2},new byte[]{3,4});
        CatReport report8 = new CatReport(
                adoption8,LocalDate.now().plusDays(-1), new byte[]{1,2},new byte[]{3,4});
        CatReport report9 = new CatReport(
                adoption9,LocalDate.now(), new byte[]{1,2},new byte[]{3,4});

        when(catAdoptionRepository.findByTrialDateGreaterThanEqual(LocalDate.now()))
                .thenReturn(List.of(adoption7, adoption8, adoption9));
        when(catReportRepository.findByDateAndPhotoIsNotNullAndTextIsNotNull(LocalDate.now()))
                .thenReturn(List.of(report9));
        when(catReportRepository.findLatestReport(adoption7.getId())).thenReturn(report7);
        when(catReportRepository.findLatestReport(adoption8.getId())).thenReturn(report8);

        //запускаем тестируемый метод
        notifier.sendWarningNoReport();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        //пользователю 7 и 8 уйдут сообщения в чат
        verify(telegramBotSender, times(2)).sendMessageToUser(
                userArgumentCaptor.capture(), stringArgumentCaptor.capture(), any(Integer.class));
        assertEquals(userArgumentCaptor.getAllValues().get(0), user7);
        assertEquals(stringArgumentCaptor.getValue(), "ВНИМАНИЕ !!! " +
                "Просим вас присылать ежедневный отчет до 21:00.");

        //а про пользователя 7 еще уйдет жалоба волонтеру
        userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageToVolunteerService, only()).createMessageToVolunteer(
                anyInt(), userArgumentCaptor.capture(), stringArgumentCaptor.capture());
        assertEquals(userArgumentCaptor.getValue(), user7);
        assertEquals("ВНИМАНИЕ !!! Данный опекун не присылал ежедневный отчет более 2 дней.", stringArgumentCaptor.getValue());
    }

    @Test
    void sendWarningNoDogReportTest() throws TelegramApiException {
        Dog dog7 = new Dog();
        Dog dog8 = new Dog();
        Dog dog9 = new Dog();
        dog7.setId(7);
        dog8.setId(8);
        dog8.setId(9);
        User user7 = new User(); //прислал последний отчет 3 дня назад
        User user8 = new User(); //прислал последний отчет вчера
        User user9 = new User(); //прислал последний отчет сегодня
        user7.setId(77);
        user8.setId(88);
        user9.setId(99);
        DogAdoption adoption7 = new DogAdoption(user7, dog7, LocalDate.now());
        adoption7.setId(777);
        DogAdoption adoption8 = new DogAdoption(user8, dog8, LocalDate.of(2023, 11, 5));
        adoption8.setId(888);
        DogAdoption adoption9 = new DogAdoption(user9, dog9, LocalDate.of(2023, 11, 5));
        adoption8.setId(999);
        DogReport report7 = new DogReport(
                adoption7,LocalDate.now().plusDays(-3), new byte[]{1,2},new byte[]{3,4});
        DogReport report8 = new DogReport(
                adoption8,LocalDate.now().plusDays(-1), new byte[]{1,2},new byte[]{3,4});
        DogReport report9 = new DogReport(
                adoption9,LocalDate.now(), new byte[]{1,2},new byte[]{3,4});

        when(dogAdoptionRepository.findByTrialDateGreaterThanEqual(LocalDate.now()))
                .thenReturn(List.of(adoption7, adoption8, adoption9));
        when(dogReportRepository.findByDateAndPhotoIsNotNullAndTextIsNotNull(LocalDate.now()))
                .thenReturn(List.of(report9));
        when(dogReportRepository.findLatestReport(adoption7.getId())).thenReturn(report7);
        when(dogReportRepository.findLatestReport(adoption8.getId())).thenReturn(report8);

        //запускаем тестируемый метод
        notifier.sendWarningNoReport();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        //пользователю 7 и 8 уйдут сообщения в чат
        verify(telegramBotSender, times(2)).sendMessageToUser(
                userArgumentCaptor.capture(), stringArgumentCaptor.capture(), any(Integer.class));
        assertEquals(userArgumentCaptor.getAllValues().get(0), user7);
        assertEquals(stringArgumentCaptor.getValue(), "ВНИМАНИЕ !!! " +
                "Просим вас присылать ежедневный отчет до 21:00.");

        //а про пользователя 7 еще уйдет жалоба волонтеру
        userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageToVolunteerService, only()).createMessageToVolunteer(
                anyInt(), userArgumentCaptor.capture(), stringArgumentCaptor.capture());
        assertEquals(userArgumentCaptor.getValue(), user7);
        assertEquals("ВНИМАНИЕ !!! Данный опекун не присылал ежедневный отчет более 2 дней.", stringArgumentCaptor.getValue());
    }

    @Test
    void sendCongratulationCatAdoptionTest() throws TelegramApiException {
        Cat cat1 = new Cat();
        Cat cat2 = new Cat();
        cat1.setId(1);
        cat1.setId(2);
        User user1 = new User();
        User user2 = new User();
        user1.setId(111111111);
        user2.setId(222222222);
        CatAdoption adoption1 = new CatAdoption(user1, cat1, LocalDate.now());
        CatAdoption adoption2 = new CatAdoption(user2, cat2, LocalDate.of(2023, 11, 5));
        when(catAdoptionRepository.findByTrialDateGreaterThanEqual(LocalDate.now())).thenReturn(List.of(adoption1, adoption2));
        notifier.sendCongratulation();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(telegramBotSender, only()).sendMessageToUser(
                userArgumentCaptor.capture(), stringArgumentCaptor.capture(), any(Integer.class));
        assertEquals(userArgumentCaptor.getValue(), user1);
        assertEquals(stringArgumentCaptor.getValue(), "Поздравляем !!! Вы успешно прошли испытательный период. " +
                "Всего наилучшего Вам и вашему питомцу.");
    }

    @Test
    void sendCongratulationDogAdoptionTest() throws TelegramApiException {

        Dog dog4 = new Dog();
        Dog dog5 = new Dog();
        dog4.setId(4);
        dog5.setId(5);
        User user4 = new User();
        User user5 = new User();
        user4.setId(444444444);
        user5.setId(555555555);
        DogAdoption adoption4 = new DogAdoption(user4, dog4, LocalDate.now());
        DogAdoption adoption5 = new DogAdoption(user5, dog5, LocalDate.of(2023,11, 5));
        when(dogAdoptionRepository.findByTrialDateGreaterThanEqual(LocalDate.now())).thenReturn(List.of(adoption4, adoption5));
        notifier.sendCongratulation();

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(telegramBotSender, only()).sendMessageToUser(
                userArgumentCaptor.capture(), stringArgumentCaptor.capture(), any(Integer.class));
        assertEquals(userArgumentCaptor.getValue(), user4);
        assertEquals(stringArgumentCaptor.getValue(), "Поздравляем !!! Вы успешно прошли испытательный период. " +
                "Всего наилучшего Вам и вашему питомцу.");
    }

    @Test
    void sendNotificationTest() throws TelegramApiException {
        Cat cat3 = new Cat();
        cat3.setId(3);
        User user3 = new User();
        user3.setId(333333333);
        Adoption adoption3 = new CatAdoption(user3, cat3, LocalDate.now());
        String notification3 = "notification3";
        notifier.sendNotification(adoption3, notification3);

        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(telegramBotSender, only()).sendMessageToUser(any(User.class), stringArgumentCaptor.capture(), any(Integer.class));
        assertEquals(stringArgumentCaptor.getValue(), notification3);
    }
}
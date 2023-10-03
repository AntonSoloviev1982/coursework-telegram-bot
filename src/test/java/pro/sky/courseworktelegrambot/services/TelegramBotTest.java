package pro.sky.courseworktelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.entities.User;
import pro.sky.courseworktelegrambot.repositories.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelegramBotTest {
    //репозитории мокаем
    @Mock
    private UserRepository userRepository;
    @Mock
    private StateRepository stateRepository;
    @Mock
    private MessageToVolunteerRepository messageToVolunteerRepository;
    @Mock
    private DogAdoptionRepository dogAdoptionRepository;
    @Mock
    private CatAdoptionRepository catAdoptionRepository;
    @Mock
    private DogReportRepository dogReportRepository;
    @Mock
    private CatReportRepository catReportRepository;
    @Mock
    private DogRepository dogRepository;
    @Mock
    private CatRepository catRepository;
    @Mock
    private ShelterService shelterService;

    //сервисы реальные - их тоже потестируем. Можно было упростить жизнь и их тоже замокать.
    @InjectMocks
    private AdoptionService adoptionService;
    @InjectMocks
    private ReportService reportService;
    @InjectMocks
    //В тестируемый бот инжектим моки и одновременно сделаем его spy-объектом
    private TelegramBot telegramBot;  //out - ObjectUnderTest

    //инициировать взаимодействие с telegramBot и собирать результат будем через spy-объект
    private TelegramBot spyTelegramBot;
    @BeforeEach
    void initSpyTelegramBot() {
        spyTelegramBot = Mockito.spy(telegramBot);
        //для работы бота передадим в него сервисы с заинжекченными моками репозиториев
        spyTelegramBot.setServices(adoptionService, reportService);
    }

    @Test
    public void onUpdateReceived_WhenNewUser() throws TelegramApiException {
        //При поиске пользователя возвратим, что не найден
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        //при посылке сообщения - ошибку не выбрасываем
        //так возникает TelegramApiException: Parameter method can not be null
        //when(spyTelegramBot.execute(any(SendMessage.class))).thenReturn(null); т.е. сначала идет ошибка, а потом задание вернуть null
        doReturn(null).when(spyTelegramBot).execute(any(SendMessage.class));

        //создадим объект - начальное состояние с кнопкой
        State initialState = new State("1","Начало",false, NamedState.INITIAL_STATE,
                Collections.singletonList(new StateButton(
                        null, "Первая кнопка", null, (byte)1,(byte)1 ,null)));

        //передадим созданное состояние в бот через мок репозитория состояний
        when(stateRepository.findByNamedState(NamedState.INITIAL_STATE)).thenReturn(initialState);
        spyTelegramBot.initStates();

        //Метод замоканного объекта UserRepository.save ничего не возвращает, далать when не нужно
        //а нужно будет проверить, какие параметры попадают ему на вход

        //создаем параметр для onUpdateReceived - в чат c id 1L пришло сообщение с id 123 c текстом 'abc'
        Message message = new Message();
        message.setChat(new Chat(1L,""));
        message.setText("abc");

        Update update = new Update();
        update.setUpdateId(123);
        update.setMessage(message);

        //Инициируем получение сообщения
        spyTelegramBot.onUpdateReceived(update);
        //Проверим, что было вызвано в spy и mock - объектах

        //Должно было быть послано 2 сообщения. Проверим это
        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class);
        verify(spyTelegramBot,times(2)).execute(sendMessageCaptor.capture());
        List<SendMessage> actualSendMessages = sendMessageCaptor.getAllValues();
        assertEquals("1", actualSendMessages.get(0).getChatId());
        assertEquals("Привет, null", actualSendMessages.get(0).getText());
        assertEquals("Начало", actualSendMessages.get(1).getText());

        //Должен быть 1 поиск в репозитории User
        verify(userRepository, times(1)).findById(1L); //times(1) можно не писать
        //Должно быть 1 сохранение в репозиторий User
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());  //если подставить user, почему-то не работает new User(1L, null, initialState)
        User actualUser = userCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий
        assertEquals(1, actualUser.getId());
        assertEquals(null, actualUser.getName());
        assertEquals(0, ChronoUnit.SECONDS.between(actualUser.getStateTime(), LocalDateTime.now()));
        assertEquals(initialState, actualUser.getState());
    }
    @Test
    public void onUpdateReceived_GoToNextState() throws TelegramApiException {
        //создадим объект - какое-то не именованное старое состояние c кодом 11 с какой-то кнопкой
        //которая переводит в новое состояние 22 c другой кнопкой
        //Напомню, что только если в новом состоянии будут кнопки переход туда состоится,
        //иначе при отсутствии кнопок пользователю бросается текст состояния, а перехода не происходит

        State newState = new State(
                "22", "Новое состояние", false, null, new ArrayList<>());
        StateButton otherButton = new StateButton(
                newState, "Другая кнопка", null, (byte)1,(byte)1 ,null);
        newState.getButtons().add(0,otherButton);

        State oldState = new State(
                "11", "Старое состояние", false, null, new ArrayList<>());
        StateButton anyButton = new StateButton(
                oldState, "Какая-то кнопка",  newState, (byte)1,(byte)1 ,null);
        oldState.getButtons().add(0, anyButton);

        //При поиске пользователя возвратим, что он найден по коду 1, в состоянии 11 (старое состояние)
        //и от него пришел текст "Какая-то кнопка". Ожидаю, что новое состояние будет 22 - новое состояние

        when(userRepository.findById(1L)).thenReturn(
                Optional.of(new User(1L,"Какой-то юзер", oldState)));
        //при посылке сообщения - ошибку не выбрасываем
        doReturn(null).when(spyTelegramBot).execute(any(SendMessage.class));

        //создаем параметр для onUpdateReceived - в чат c id 1L пришло сообщение c текстом 'Какая-то кнопка'
        Message message = new Message();
        message.setChat(new Chat(1L,""));
        message.setText("Какая-то кнопка");

        Update update = new Update();
        //update.setUpdateId(123);
        update.setMessage(message);

        //Инициируем получение сообщения
        spyTelegramBot.onUpdateReceived(update);
        //Проверим, что было вызвано в spy и mock - объектах

        //Должно было быть послано 1 сообщение. Проверим это
        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class); //создаем ловца SendMessage
        verify(spyTelegramBot).execute(sendMessageCaptor.capture()); //times(1) можем не указывать
        SendMessage actualSendMessage = sendMessageCaptor.getValue();  //вынем из ловца, то что он словил
        assertEquals("1", actualSendMessage.getChatId()); //проверяем, что сообщение послано пользователю 1
        assertEquals("Новое состояние", actualSendMessage.getText());

        //Должен быть 1 поиск в репозитории
        verify(userRepository).findById(1L); //times(1) можно не писать
        //Должно быть 1 сохранение в репозиторий
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий
        assertEquals(1, actualUser.getId());
        assertEquals("Какой-то юзер", actualUser.getName());
        assertEquals(0, ChronoUnit.SECONDS.between(actualUser.getStateTime(), LocalDateTime.now()));
        assertEquals(newState, actualUser.getState());
    }
    @Test
    public void onUpdateReceived_MessageToVolunteer() throws TelegramApiException {
        //создадим объект - состояние messageToVolunteer
        State state = new State("MessageToVolunteer",
                "Введите сообщение для волонтера - это пользователь уже получил. Ждем, что пришлет",
                true, NamedState.MESSAGE_TO_VOLUNTEER, null);

        //При поиске пользователя возвратим, что он найден по коду 1, в состоянии MessageToVolunteer
        //и от него пришел текст "Что собаки едят?".
        //Ожидаю, что новое состояние останется прежним - MessageToVolunteer
        //а в базу запишется новая запись для волонтера. Id записи = Id пришедшего Message

        when(userRepository.findById(1L)).thenReturn(
                Optional.of(new User(1L,"Какой-то юзер", state)));

        //создаем параметр для onUpdateReceived - в чат c id 1L пришло сообщение c текстом 'Что собаки едят?'
        Message message = new Message();
        message.setMessageId(123);
        message.setChat(new Chat(1L,""));
        message.setText("Что собаки едят?");
        Update update = new Update();
        update.setMessage(message);

        //Инициируем получение сообщения
        spyTelegramBot.onUpdateReceived(update);

        //Никаких сообщений не должно быть послано. Проверим это
        verify(spyTelegramBot, never()).execute(any(SendMessage.class));

        //Должен быть 1 поиск User
        verify(userRepository).findById(1L); //times(1) можно не писать
        //Проверим, что сохранено в User
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий User
        assertEquals(1, actualUser.getId());
        assertEquals("Какой-то юзер", actualUser.getName());
        assertEquals(0, ChronoUnit.SECONDS.between(actualUser.getStateTime(), LocalDateTime.now()));
        assertEquals(state, actualUser.getState());

        //Проверим, что сохранено в MessageToVolunteer
        ArgumentCaptor<MessageToVolunteer> messageToVolunteerCaptor = ArgumentCaptor.forClass(MessageToVolunteer.class);
        verify(messageToVolunteerRepository).save(messageToVolunteerCaptor.capture());
        MessageToVolunteer actualMessageToVolunteer = messageToVolunteerCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий id, user, questionTime, question
        assertEquals(123, actualMessageToVolunteer.getId());
        assertEquals(actualUser, actualMessageToVolunteer.getUser());
        assertEquals(0, ChronoUnit.SECONDS.between(actualMessageToVolunteer.getQuestionTime(), LocalDateTime.now()));
        assertEquals("Что собаки едят?", actualMessageToVolunteer.getQuestion());
    }

    @Test
    public void onUpdateReceived_Feedback() throws TelegramApiException {
        //создадим объект - состояние messageToVolunteer
        State state = new State("MessageToVolunteer",
                "Введите сообщение для волонтера - это пользователь уже получил. Ждем, что пришлет",
                true, NamedState.MESSAGE_TO_VOLUNTEER, null);

        //При поиске пользователя возвратим, что он найден по коду 1, в состоянии MessageToVolunteer
        //и от него пришел текст "Что собаки едят?".
        //Ожидаю, что новое состояние останется прежним - MessageToVolunteer
        //а в базу запишется новая запись для волонтера. Id записи = Id пришедшего Message

        when(userRepository.findById(1L)).thenReturn(
                Optional.of(new User(1L,"Какой-то юзер", state)));

        //создаем параметр для onUpdateReceived - в чат c id 1L пришло сообщение c текстом 'Что собаки едят?'
        Message message = new Message();
        message.setMessageId(123);
        message.setChat(new Chat(1L,""));
        message.setText("Что собаки едят?");
        Update update = new Update();
        update.setMessage(message);

        //Инициируем получение сообщения
        spyTelegramBot.onUpdateReceived(update);

        //Никаких сообщений не должно быть послано. Проверим это
        verify(spyTelegramBot, never()).execute(any(SendMessage.class));

        //Должен быть 1 поиск User
        verify(userRepository).findById(1L); //times(1) можно не писать
        //Проверим, что сохранено в User
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий User
        assertEquals(1, actualUser.getId());
        assertEquals("Какой-то юзер", actualUser.getName());
        assertEquals(0, ChronoUnit.SECONDS.between(actualUser.getStateTime(), LocalDateTime.now()));
        assertEquals(state, actualUser.getState());

        //Проверим, что сохранено в MessageToVolunteer
        ArgumentCaptor<MessageToVolunteer> messageToVolunteerCaptor = ArgumentCaptor.forClass(MessageToVolunteer.class);
        verify(messageToVolunteerRepository).save(messageToVolunteerCaptor.capture());
        MessageToVolunteer actualMessageToVolunteer = messageToVolunteerCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий id, user, questionTime, question
        assertEquals(123, actualMessageToVolunteer.getId());
        assertEquals(actualUser, actualMessageToVolunteer.getUser());
        assertEquals(0, ChronoUnit.SECONDS.between(actualMessageToVolunteer.getQuestionTime(), LocalDateTime.now()));
        assertEquals("Что собаки едят?", actualMessageToVolunteer.getQuestion());
    }

    @Test
    public void onUpdateReceived_Report() throws TelegramApiException {
        //создадим объект - состояние Report - ждем отчет
        State state = new State("Report",
                "Пришлите файлы отчета - это пользователь уже получил. Ждем, что пришлет",
                true, NamedState.REPORT, null);

        //При поиске пользователя возвратим, что он найден по коду 1, в состоянии Report
        //и от него пришел файл  *.jpg.
        //Ожидаю, что новое состояние останется прежним - Report
        //а в базу запишется запись для нового отчета.

        User user = new User(1L,"Какой-то юзер", state);
        user.setShelterId(ShelterId.DOG);
        DogAdoption adoption = new DogAdoption(user, new Dog(), LocalDate.of(2024,1,1));
        adoption.setId(11);
        DogReport report = new DogReport(adoption, LocalDate.now(), null, null);
        report.setId(111);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(dogReportRepository.save(any())).thenReturn(report);
        //у пользователя есть активное усыновление. API волонтера следит, чтобы такое было только одно
        when(dogAdoptionRepository.findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(any(),any(),any()))
                .thenReturn(Collections.singletonList(adoption));
        //Усыновление-Дата - уникальный индекс таблицы. Двух таких комбинаций быть не может
        when(dogReportRepository.findByAdoptionAndDate(any(), any()))
                .thenReturn(Collections.singletonList(report));
        //при посылке сообщения - ошибку не выбрасываем
        doReturn(null).when(spyTelegramBot).execute(any(SendMessage.class));

        //создаем параметр для onUpdateReceived - в чат c id 1L пришло сообщение c фото
        Message message = new Message();
        message.setMessageId(123);
        message.setChat(new Chat(1L,""));
        PhotoSize photoSize = new PhotoSize();
        photoSize.setFileId("1111");
        message.setPhoto(Collections.singletonList(photoSize));
        Update update = new Update();
        update.setMessage(message);

        //Инициируем получение сообщения
        spyTelegramBot.onUpdateReceived(update);

        //В ответ должно было быть послано 1 сообщение. Проверим это
        ArgumentCaptor<SendMessage> sendMessageCaptor = ArgumentCaptor.forClass(SendMessage.class); //создаем ловца SendMessage
        verify(spyTelegramBot).execute(sendMessageCaptor.capture()); //times(1) можем не указывать
        SendMessage actualSendMessage = sendMessageCaptor.getValue();  //вынем из ловца, то что он словил
        assertEquals("1", actualSendMessage.getChatId()); //проверяем, что сообщение послано пользователю 1
        assertEquals("Осталось прислать текст", actualSendMessage.getText());

        //Должен быть 1 поиск User
        verify(userRepository).findById(1L); //times(1) можно не писать
        //Проверим, что сохранено в User
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий User
        assertEquals(1, actualUser.getId());
        assertEquals("Какой-то юзер", actualUser.getName());
        assertEquals(0, ChronoUnit.SECONDS.between(actualUser.getStateTime(), LocalDateTime.now()));
        assertEquals(state, actualUser.getState());

        //Проверим, что сохранено в Report
        ArgumentCaptor<DogReport> reportCaptor = ArgumentCaptor.forClass(DogReport.class);
        verify(dogReportRepository).save(reportCaptor.capture());
        DogReport actualReport = reportCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий Report - id, adoption, date, photo, text
        assertEquals(111, actualReport.getId());
        assertEquals(adoption, actualReport.getAdoption());
        assertEquals(LocalDate.now(), actualReport.getDate());
        assertArrayEquals(photoSize.toString().getBytes(), actualReport.getPhoto());
        assertEquals(null, actualReport.getText());
    }
}

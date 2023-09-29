package pro.sky.courseworktelegrambot.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.courseworktelegrambot.entities.NamedState;
import pro.sky.courseworktelegrambot.entities.State;
import pro.sky.courseworktelegrambot.entities.StateButton;
import pro.sky.courseworktelegrambot.repositories.StateRepository;
import pro.sky.courseworktelegrambot.repositories.UserRepository;
import pro.sky.courseworktelegrambot.entities.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TelegramBotTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private StateRepository stateRepository;
    @InjectMocks
    //Мы в него инжектим моки и одновременно сделаем его spy-объектом
    private TelegramBot telegramBot;  //out - ObjectUnderTest

    @Test
    public void onUpdateReceivedWhenNewUser() throws TelegramApiException {
        TelegramBot spyTelegramBot = Mockito.spy(telegramBot);
        //При поиске пользователя возвратим, что не найден
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        //при посылке сообщения - ошибку не выбрасываем
        //так возникает TelegramApiException: Parameter method can not be null
        //when(spyTelegramBot.execute(any(SendMessage.class))).thenReturn(null); т.е. сначала идет ошибка, а потом задание вернуть null
        doReturn(null).when(spyTelegramBot).execute(any(SendMessage.class));

        //создадим объект - начальное состояние с кнопкой
        State initialState = new State("1","Начало",false, NamedState.INITIAL_STATE,
                Collections.singletonList(new StateButton(
                        "1", "Первая кнопка", null, (byte)1,(byte)1 ,null)));
        //передадим созданное состояние в бот через мок репозитория состояний
        when(stateRepository.findByNamedState(NamedState.INITIAL_STATE)).thenReturn(initialState);
        spyTelegramBot.initStates();

        //Метод замоканного объекта repository.save ничего не возвращает, далать when не нужно
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

        //Должно быть 1 поиск и 1 сохранение в репозиторий
        verify(userRepository, times(1)).findById(1L); //times(1) можно не писать
        verify(userRepository, times(1)).save(any()); //если подставить user, почему-то не работает new User(1L, null, initialState)

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository).save(userCaptor.capture());
        User actualUser = userCaptor.getValue();
        //проверяем поля объекта, отправленного в репозиторий
        assertEquals(1, actualUser.getId());
        assertEquals(null, actualUser.getName());
        assertEquals(0, ChronoUnit.SECONDS.between(actualUser.getStateTime(), LocalDateTime.now()));
        assertEquals(initialState, actualUser.getState());
    }
}

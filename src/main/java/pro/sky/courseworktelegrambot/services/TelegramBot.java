package pro.sky.courseworktelegrambot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.courseworktelegrambot.config.BotConfig;

@Slf4j
@Service
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;

    public TelegramBot(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    /**
     * Получает имя бота, которое будет использоваться при регистрации на платформе Telegram.
     *
     * @return Имя бота.
     */
    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    /**
     * Получает токен бота, который будет использоваться для аутентификации на платформе Telegram.
     *
     * @return Токен бота.
     */
    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String firstName = update.getMessage().getChat().getFirstName();

            if (text.equals("/start")) {
                String textToSend = "Hello " + firstName + "!";
                sendMessage(chatId, textToSend);
            }

        }


    }

    /**
     * Отправляет сообщение указанному чату с заданным текстовым сообщением.
     *
     * @param chatId      Идентификатор чата, куда нужно отправить сообщение.
     * @param textToSend  Текст сообщения, который следует отправить.
     */
    public void sendMessage(long chatId, String textToSend) {
        // Создание объекта SendMessage для отправки сообщения.
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        try {
            // Попытка отправить сообщение через Telegram API.
            execute(sendMessage);
        } catch (TelegramApiException e) {
            // В случае возникновения ошибки, записываем сообщение об ошибке в журнал.
            log.error("Error occurred: " + e.getMessage());
        }

    }


}

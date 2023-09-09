package pro.sky.courseworktelegrambot.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pro.sky.courseworktelegrambot.services.TelegramBot;

@Slf4j
@Component
public class BotInitializer {

    @Autowired
    TelegramBot bot;

    /**
     * Метод инициализации приложения, который реагирует на событие ContextRefreshedEvent.
     * При вызове данного метода, он создает экземпляр TelegramBotsApi, затем регистрирует бота с этим API.
     *
     * @throws TelegramApiException если произошла ошибка при регистрации бота.
     */
    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            // Создание экземпляра TelegramBotsApi с использованием DefaultBotSession.
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            // Регистрация бота с TelegramBotsApi.
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            //не могу найти ошибку log.error("Error occurred: " + e.getMessage());
            System.out.println("Error occurred: " + e.getMessage());
        }
    }
}

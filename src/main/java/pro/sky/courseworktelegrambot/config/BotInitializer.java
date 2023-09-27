package pro.sky.courseworktelegrambot.config;

//import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import pro.sky.courseworktelegrambot.services.TelegramBot;

//@Slf4j
@Component
public class BotInitializer {
    private static final Logger logger = LoggerFactory.getLogger(BotInitializer.class);

    TelegramBot bot;

    public BotInitializer(TelegramBot bot) {
        this.bot = bot;
    }

    /**
     * Метод инициализации приложения, который реагирует на событие ContextRefreshedEvent.
     * При вызове данного метода, он создает экземпляр TelegramBotsApi, затем регистрирует бота с этим API.
     */
    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            // Создание экземпляра TelegramBotsApi с использованием DefaultBotSession.
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            // Регистрация бота с TelegramBotsApi.
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            logger.error("Error of creation or registration of bot occurred: " + e.getMessage());
        }
    }
}

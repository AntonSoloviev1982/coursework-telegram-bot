package pro.sky.courseworktelegrambot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


/**
 * Класс нужен для использования имени и токена телеграмм бота, чтобы подключиться к нему.
 */
@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {

    @Value("${telegram.bot.name}")
    private String botName;

    @Value("${telegram.bot.token}")
    private String botToken;

}


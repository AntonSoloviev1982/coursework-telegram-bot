package pro.sky.courseworktelegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CourseworkTelegramBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseworkTelegramBotApplication.class, args);
    }

}

package pro.sky.courseworktelegrambot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TelegramBotExceptionHandler {

    @ExceptionHandler({MessageToVolunteerNotFoundException.class, ShelterNotFoundException.class})
    public ResponseEntity<?> handleNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}

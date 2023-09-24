package pro.sky.courseworktelegrambot.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ShelterExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ShelterExceptionHandler.class);

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<?> handlerNotFoundItem(NoSuchElementException e){
        logger.error("Element by this Id is absent.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Element by this Id is absent. " + e.getMessage());
    }
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity<?> handlerEntityNotFound(EntityNotFoundException e){
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity by this Id not found. " + e.getMessage());
    }
    @ExceptionHandler({TelegramApiException.class})
    public ResponseEntity<?> handlerTelegramError(TelegramApiException e){
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("TelegramError " + e.getMessage());
    }
    @ExceptionHandler({UserIsBusyException.class})
    public ResponseEntity<?> UserIsBusyError(UserIsBusyException e){
        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already has a trial period. " + e.getMessage());
    }

}

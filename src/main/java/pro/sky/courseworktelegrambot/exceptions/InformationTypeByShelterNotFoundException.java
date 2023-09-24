package pro.sky.courseworktelegrambot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InformationTypeByShelterNotFoundException extends RuntimeException{

    private final String informationType;

    public InformationTypeByShelterNotFoundException(String informationType) {
        this.informationType = informationType;
    }

    @Override
    public String getMessage() {
        return "Field by Shelter: " + informationType + " is not found!";
    }
}

package pro.sky.courseworktelegrambot.exceptions;

public class UserIsBusyException extends RuntimeException {

    @Override
    public String getMessage() {
        return "User is busy!";
    }
}

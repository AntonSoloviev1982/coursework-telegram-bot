package pro.sky.courseworktelegrambot.exceptions;

public class UserOrPetIsBusyException extends RuntimeException {

    @Override
    public String getMessage() {
        return "User is busy!";
    }
}

package pro.sky.courseworktelegrambot.exceptions;

public class MessageToVolunteerNotFoundException extends RuntimeException{

    private final int id;

    public MessageToVolunteerNotFoundException(int id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "MessageToVolunteer with id: " + id + " is not found!";
    }
}

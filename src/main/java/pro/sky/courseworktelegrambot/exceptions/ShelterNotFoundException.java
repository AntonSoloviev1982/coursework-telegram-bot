package pro.sky.courseworktelegrambot.exceptions;

public class ShelterNotFoundException extends RuntimeException{

    private final int id;

    public ShelterNotFoundException(int id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Shelter with id: " + id + " is not found!";
    }
}

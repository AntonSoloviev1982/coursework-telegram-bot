package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "dog_report")
public class DogReport extends Report {
    @ManyToOne
    private DogAdoption adoption;
    @Override
    public DogAdoption getAdoption() {
        return adoption;
    }

    public DogReport(DogAdoption adoption, LocalDate date, byte[] photo, byte[] text) {
        super(date, photo, text);
        this.adoption = adoption;
    }
    public DogReport() {
    }
    @Override
    public String toString() {
        return "DogReport{" +
                "id=" + getId() +
                ", adoption=" + adoption +
                ", date=" + getDate() +
                ", textPresented=" + getTextPresented() +
                ", photoPresented=" + getPhotoPresented() +
                "}";
    }
}


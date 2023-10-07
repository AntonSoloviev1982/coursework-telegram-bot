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

    public DogReport(DogAdoption adoption, LocalDate date, byte[] photo,
                     String imageType,int imageSize,String text) {
        super(date, photo,imageType,imageSize, text);
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


package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cat_report")
public class CatReport extends Report {
    @ManyToOne
    private CatAdoption adoption;
    @Override
    public CatAdoption getAdoption() {
        return adoption;
    }

    public CatReport(CatAdoption adoption, LocalDate date, byte[] photo, byte[] text) {
        super(date, photo, text);
        this.adoption = adoption;
    }
    public CatReport() {
    }
    @Override
    public String toString() {
        return "CatReport{" +
                "id=" + getId() +
                ", adoption=" + adoption +
                ", date=" + getDate() +
                ", textPresented=" + getTextPresented() +
                ", photoPresented=" + getPhotoPresented() +
                "}";
    }

}


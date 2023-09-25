package pro.sky.courseworktelegrambot.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
/**
 * Object Report
 */
@MappedSuperclass
public abstract class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDate date; // дата отчета
    @Lob
    private byte[] photo; // фото отчета
    @Lob
    private byte[] text; // комментарий отчета

    public Report(LocalDate date, byte[] photo, byte[] text) {
        this.date = date;
        this.photo = photo;
        this.text = text;
    }

    public Report() {
    }

    public int getId() {
        return id;
    }

    public abstract Adoption getAdoption();

    public LocalDate getDate() {
        return date;
    }

    public void setReportDate(LocalDate date) {
        this.date = date;
    }
    @JsonIgnore
    public byte[] getPhoto() {
        return photo;
    }

    public boolean photoIsPresent(){
        return photo != null;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
    @JsonIgnore
    public byte[]  getText() {
        return text;
    }
    public boolean textIsPresent(){
        return text != null;
    }

    public void setText(byte[]  text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return id == report.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", date=" + date +
                ", textIsPresent=" + textIsPresent() +
                ", photoIsPresent=" + photoIsPresent() +
                '}';
    }
}

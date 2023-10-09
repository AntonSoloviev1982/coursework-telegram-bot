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
    @Column(columnDefinition = "oid")
    private byte[] photo; // фото отчета
    private String imageType; //расширение фото
    private int imageSize; //размер фото
    private String text; // текст отчета

    public Report(LocalDate date, byte[] photo, String imageType,
                  int imageSize, String text) {
        this.date = date;
        this.photo = photo;
        this.text = text;
        this.imageType = imageType;
        this.imageSize = imageSize;
    }

    public Report() {
    }

    public void setId(int id) {
        this.id = id;
    } // для тестов

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

    public boolean getPhotoPresented(){
        return photo != null;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getText() {
        return text;
    }
    public boolean getTextPresented(){
        return text != null;
    }

    public void setText(String text) {
        this.text = text;
    }


    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
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
                ", textPresented=" + getTextPresented() +
                ", photoPresented=" + getPhotoPresented() +
                '}';
    }
}

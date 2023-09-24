package pro.sky.courseworktelegrambot.entities;

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
    protected int id;
    private int adoptionId; // ID усыновления
    private LocalDate date; // дата отчета
    @Lob
    private byte[] photo; // фото отчета
    @Lob
    private byte[] text; // комментарий отчета

    public Report(int adoptionId, LocalDate date, byte[] photo, byte[] text) {
        this.adoptionId = adoptionId;
        this.date = date;
        this.photo = photo;
        this.text = text;
    }

    public Report() {
    }

    public int getId() {
        return id;
    }

    public int getAdoptionId() {
        return adoptionId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setReportDate(LocalDate date) {
        this.date = date;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public byte[]  getText() {
        return text;
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
                ", adoptionId=" + adoptionId +
                ", date=" + date +
                ", text='" + text + '\'' +
                '}';
    }
}

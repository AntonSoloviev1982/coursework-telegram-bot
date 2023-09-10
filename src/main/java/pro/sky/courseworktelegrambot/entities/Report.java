package pro.sky.courseworktelegrambot.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
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
    private LocalDate reportDate; // дата отчета
    private byte[] photo; // фото отчета
    private String text; // комментарий отчета

    public Report(int adoptionId, LocalDate reportDate, byte[] photo, String text) {
        this.adoptionId = adoptionId;
        this.reportDate = reportDate;
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

    public void setAdoptionId(int adoptionId) {
        this.adoptionId = adoptionId;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
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
                ", reportDate=" + reportDate +
                ", text='" + text + '\'' +
                '}';
    }
}

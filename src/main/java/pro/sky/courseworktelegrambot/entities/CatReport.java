package pro.sky.courseworktelegrambot.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cat_report")
public class CatReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adoptionId;

    private LocalDate reportDate;

    @Lob
    private byte[] photo;

    private String text;

    public CatReport(Long id, Long adoptionId, LocalDate reportDate, byte[] photo, String text) {
        this.id = id;
        this.adoptionId = adoptionId;
        this.reportDate = reportDate;
        this.photo = photo;
        this.text = text;
    }

    public CatReport() {
    }

    public Long getId() {
        return id;
    }

    public Long getAdoptionId() {
        return adoptionId;
    }

    public void setAdoptionId(Long adoptionId) {
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

}

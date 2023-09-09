package pro.sky.courseworktelegrambot.services;

import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entities.DogReport;
import pro.sky.courseworktelegrambot.repositories.DogReportRepository;
import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Optional;

@Service
public class DogReportService {
    private final DogReportRepository dogReportRepository;

    public DogReportService(DogReportRepository dogReportRepository) {
        this.dogReportRepository = dogReportRepository;
    }


    public DogReport createDogReport(DogReport dogReport) {
        return dogReportRepository.save(dogReport);
    }

    public DogReport getDogReport(Integer id) {
        return dogReportRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dog with id " + id + " not found"));
    }

    public DogReport updateDogReport(DogReport dogReport) {
        DogReport dogReportFromDB = getDogReport(dogReport.getId());
        dogReportFromDB.setAdoptionId(dogReport.getAdoptionId());
        dogReportFromDB.setReportDate(dogReport.getReportDate());
        dogReportFromDB.setPhoto(dogReport.getPhoto());
        dogReportFromDB.setText(dogReport.getText());
        return dogReportRepository.save(dogReport);
    }

    public void deleteDogReport(Integer id) {
        Optional<DogReport> dogOptional = dogReportRepository.findById(id);
        if (dogOptional.isPresent()) {
            dogReportRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Dog with id " + id + " not found");
        }
    }
    public Collection<DogReport> getAllDogReports(){
        return dogReportRepository.findAll();
    }
}

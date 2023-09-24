package pro.sky.courseworktelegrambot.services;

import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entities.CatReport;
import pro.sky.courseworktelegrambot.repositories.CatReportRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class CatReportService {

    private final CatReportRepository catReportRepository;

    public CatReportService(CatReportRepository catReportRepository) {
        this.catReportRepository = catReportRepository;
    }

    public CatReport createCatReport(CatReport catReport) {
        return catReportRepository.save(catReport);
    }

    public CatReport getCatReportById(int id) {
        return catReportRepository.findById(id).get();
    }

    public CatReport updateCatReport(CatReport catReport) {
        return catReportRepository.save(catReport);
    }

    public CatReport deleteCatReport(int id) {
        CatReport catReport = catReportRepository.findById(id).get();
        catReportRepository.deleteById(id);
        return catReport;
    }

    public List<CatReport> readByDate(LocalDate localDate){
        return List.copyOf(catReportRepository.findAllByDate(localDate));
    }

    //public List<CatReport> readAllById(int id){
    //    return List.copyOf(catReportRepository.findAllById(id));
    //}

}

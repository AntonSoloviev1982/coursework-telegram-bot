package pro.sky.courseworktelegrambot.services;

import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entity.CatReport;
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

    public CatReport getCatReportById(Long id) {
        return catReportRepository.findById(id).get();
    }

    public CatReport updateCatReport(CatReport catReport) {
        return catReportRepository.save(catReport);
    }

    public CatReport deleteCatReport(Long id) {
        CatReport catReport = catReportRepository.findById(id).get();
        catReportRepository.deleteById(id);
        return catReport;
    }

    public List<CatReport> readByDate(LocalDate localDate){
        return List.copyOf(catReportRepository.findAllByReportDate(localDate));
    }

    public List<CatReport> readAllById(Long id){
        return List.copyOf(catReportRepository.findAllById(id));
    }

}

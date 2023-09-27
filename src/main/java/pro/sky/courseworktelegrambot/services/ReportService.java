package pro.sky.courseworktelegrambot.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.repositories.CatReportRepository;
import pro.sky.courseworktelegrambot.repositories.CatAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.DogAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.DogReportRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final DogReportRepository dogReportRepository;
    private final CatReportRepository catReportRepository;
    private final DogAdoptionRepository dogAdoptionRepository;
    private final CatAdoptionRepository catAdoptionRepository;
    private final ShelterService shelterService;

    public ReportService(CatReportRepository catReportRepository,
                         DogReportRepository dogReportRepository,
                         CatAdoptionRepository catAdoptionRepository,
                         DogAdoptionRepository dogAdoptionRepository,
                         ShelterService shelterService) {
        this.dogReportRepository = dogReportRepository;
        this.catReportRepository = catReportRepository;
        this.dogAdoptionRepository = dogAdoptionRepository;
        this.catAdoptionRepository = catAdoptionRepository;
        this.shelterService = shelterService;
    }

    //из такого репозитория удается прочитать, возвращается предок
    //но в него ничего не удается сохранить уже при компиляции, при подстановке любого типа возникает
    //method save in interface org.springframework.data.repository.CrudRepository<T,ID> cannot be applied to given types;
    private JpaRepository<? extends Report, Integer> reportRepository(ShelterId shelterId) {
        return (shelterId == ShelterId.DOG) ? dogReportRepository : catReportRepository;
    }

    public Report saveReport(User user, byte[] photo, byte[] text) {
        //вызывается из бота, волонтер отчеты только читает

        LocalDate date = LocalDate.now();
        ShelterId shelterId = user.getShelterId();

        if (shelterId == ShelterId.DOG) {
            //Ищем у пользователя активный испытательный срок на сегодня
            List<DogAdoption> adoptionList = dogAdoptionRepository.findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                    user, date, date);
            if (adoptionList.isEmpty()) {
                //если сюда попали - это ошибка алгоритма
                //нельзя переводить пользователя в режим приема отчета,
                //если у него нет активного испытательного срока
                //Записать в лог
                return null;
            } else { //Ищем отчет за сегодня
                //если список усыновлений не пустой, то в нем должна найтись ровно 1 запись
                DogAdoption adoption = adoptionList.get(0);
                List<DogReport> reportList = dogReportRepository.findByAdoptionAndDate(adoption, date);
                DogReport report;  //объект для сохранения
                if (reportList.isEmpty()) {
                    report = new DogReport(adoption, date, photo, text);
                } else {
                    report = reportList.get(0);
                    if (photo != null) {report.setPhoto(photo);}
                    if (text != null) {report.setText(text);}
                }
                return dogReportRepository.save(report);
            }
        } else {
            List<CatAdoption> adoptionList = catAdoptionRepository.findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                    user, date, date);
            if (adoptionList.isEmpty()) {
                return null;
            } else {
                CatAdoption adoption = adoptionList.get(0);
                List<CatReport> reportList = catReportRepository.findByAdoptionAndDate(adoption, date);
                CatReport report;  //объект для сохранения
                if (reportList.isEmpty()) {
                    report = new CatReport(adoption, date, photo, text);
                } else {
                    report = reportList.get(0);
                    if (photo != null) {report.setPhoto(photo);}
                    if (text != null) {report.setText(text);}
                }
                return catReportRepository.save(report);
            }
        }
    }

    public Report getReportById(ShelterId shelterId, int reportId) {
        shelterService.checkShelterId(shelterId);
        return reportRepository(shelterId).findById(reportId).orElseThrow(() ->
                new EntityNotFoundException("Report with id " + reportId + " in shelter " + shelterId + " not found"));
    }

    public Report deleteReportById(ShelterId shelterId, int reportId) {
        shelterService.checkShelterId(shelterId);
        Report report = getReportById(shelterId, reportId);
        reportRepository(shelterId).deleteById(reportId);
        return report;
    }

    public List<Report> getAllReportsByDate(ShelterId shelterId, LocalDate date){
        shelterService.checkShelterId(shelterId);
        if (shelterId==ShelterId.DOG) {
            return List.copyOf(dogReportRepository.findByDate(date));
        } else {
            return List.copyOf(catReportRepository.findByDate(date));
        }
    }

    public List<Report> getAllReports(ShelterId shelterId){
        shelterService.checkShelterId(shelterId);
        return List.copyOf(reportRepository(shelterId).findAll());
    }


}

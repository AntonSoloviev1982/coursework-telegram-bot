package pro.sky.courseworktelegrambot.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.exceptions.ShelterNotFoundException;
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

    /**
     * Метод сохраняет отчет по собаке или кошке в ДБ .<br>
     * Используется метод репозитория {@link JpaRepository#save(Object)}.<br>
     * Используется метод коллекции {@link List#isEmpty()}
     *
     * @param user  идентификатор приюта.
     * @param photo фото отчета
     * @param text  комментарий отчета
     * @return сохраненные данные отчета для кошки или собаки
     */
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
                    if (photo != null) {
                        report.setPhoto(photo);
                    }
                    if (text != null) {
                        report.setText(text);
                    }
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
                    if (photo != null) {
                        report.setPhoto(photo);
                    }
                    if (text != null) {
                        report.setText(text);
                    }
                }
                return catReportRepository.save(report);
            }
        }
    }

    /**
     * Метод выводит отчет по индентификатору.<br>
     * Используется если значение не найдено возвращает исключение {@link java.util.Optional#orElseThrow}.<br>
     * Используется метод коллекции {@link List#isEmpty()}
     *
     * @param shelterId идентификатор приюта.
     * @param reportId  фото отчета
     * @return возвращает отчет
     * @throws ShelterNotFoundException если id отчета не найден в базе
     * @throws EntityNotFoundException  если id отчета не найден в базе
     */
    public Report getReportById(ShelterId shelterId, int reportId) {
        shelterService.checkShelterId(shelterId);
        return reportRepository(shelterId).findById(reportId).orElseThrow(() ->
                new EntityNotFoundException("Report with id " + reportId + " in shelter " + shelterId + " not found"));
    }

    /**
     * Метод удаляет отчет по заданному индентификатору.<br>
     *
     * @param shelterId идентификатор приюта.
     * @param reportId  фото отчета
     * @return возвращает удаленный отчет
     * @throws ShelterNotFoundException если id отчета не найден в базе
     * @throws EntityNotFoundException  если id отчета не найден в базе
     */
    public Report deleteReportById(ShelterId shelterId, int reportId) {
        shelterService.checkShelterId(shelterId);
        Report report = getReportById(shelterId, reportId);
        reportRepository(shelterId).deleteById(reportId);
        return report;
    }

    /**
     * Метод выводит все отчеты по дате из БД кошек или собак.<br>
     *
     * @param shelterId идентификатор приюта.
     * @param date      дата отчета
     * @return возвращает список отчетов кошек или собак
     * @throws ShelterNotFoundException если id отчета не найден в базе
     * @throws EntityNotFoundException  если id отчета не найден в базе
     */
    public List<Report> getAllReportsByDate(ShelterId shelterId, LocalDate date) {
        shelterService.checkShelterId(shelterId);
        if (shelterId == ShelterId.DOG) {
            return List.copyOf(dogReportRepository.findByDate(date));
        } else {
            return List.copyOf(catReportRepository.findByDate(date));
        }
    }

    /**
     * Метод выводит все отчеты кошек и собак из БД.<br>
     *
     * @param shelterId идентификатор приюта.
     * @return возвращает список отчетов кошек и собак
     * @throws ShelterNotFoundException если id отчета не найден в базе
     * @throws EntityNotFoundException  если id отчета не найден в базе
     */
    public List<Report> getAllReports(ShelterId shelterId) {
        shelterService.checkShelterId(shelterId);
        return List.copyOf(reportRepository(shelterId).findAll());
    }

}

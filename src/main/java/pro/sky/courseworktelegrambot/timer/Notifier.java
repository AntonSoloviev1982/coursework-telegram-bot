package pro.sky.courseworktelegrambot.timer;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.repositories.*;
import pro.sky.courseworktelegrambot.services.TelegramBot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class Notifier {

    private final CatAdoptionRepository catAdoptionRepository;
    private final DogAdoptionRepository dogAdoptionRepository;
    private final CatReportRepository catReportRepository;
    private final DogReportRepository dogReportRepository;
    private final MessageToVolunteerRepository messageToVolunteerRepository;
    private final TelegramBot telegramBot;

    public Notifier(CatAdoptionRepository catAdoptionRepository,
                    DogAdoptionRepository dogAdoptionRepository,
                    CatReportRepository catReportRepository,
                    DogReportRepository dogReportRepository,
                    MessageToVolunteerRepository messageToVolunteerRepository,
                    TelegramBot telegramBot) {
        this.catAdoptionRepository = catAdoptionRepository;
        this.dogAdoptionRepository = dogAdoptionRepository;
        this.catReportRepository = catReportRepository;
        this.dogReportRepository = dogReportRepository;
        this.messageToVolunteerRepository = messageToVolunteerRepository;
        this.telegramBot = telegramBot;
    }

    @Scheduled(cron = "0 1 21 * * *")
    private void sendWarningNoReport() throws TelegramApiException {

        long today = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.of(2022, 12,31));
        long dayLatestReport;
        LocalDate date;
        Report report;

        List<DogAdoption> currentDogAdoptionList = dogAdoptionRepository.findByTrialDateGreaterThanEqual(LocalDate.now());
        Map<Adoption, LocalDate> todayDogReports = dogReportRepository.findByDateAndPhotoIsNotNullAndTextIsNotNull(LocalDate.now())
                .stream()
                .collect(Collectors.toMap(DogReport::getAdoption, Report::getDate));
        for (DogAdoption adoption:currentDogAdoptionList) {
            if (todayDogReports.containsKey(adoption)) {
                break;
            }else{
                report = dogReportRepository.findLatestReport(adoption);
                date = (report != null) ? report.getDate() : adoption.getDate();
                dayLatestReport = ChronoUnit.DAYS.between(date, LocalDate.of(2022, 12,31));
                if(today-dayLatestReport > 2){
                    MessageToVolunteer notification = new MessageToVolunteer();
                    notification.setUser(adoption.getUser());
                    notification.setQuestionTime(LocalDateTime.now());
                    notification.setQuestion("ВНИМАНИЕ !!! Данный опекун не присылал ежедневный отчет более 2 дней");
                    messageToVolunteerRepository.save(notification);
                }else {
                    telegramBot.sendMessageToUser(adoption.getUser(), "ВНИМАНИЕ !!! " +
                            "Просим вас присылать ежедневный отчет до 21:00.", 1000000);
                }
            }
        }

        List<CatAdoption> currentCatAdoptionList = catAdoptionRepository.findByTrialDateGreaterThanEqual(LocalDate.now());
        Map<Adoption, LocalDate> todayCatReports = catReportRepository.findByDateAndPhotoIsNotNullAndTextIsNotNull(LocalDate.now())
                .stream()
                .collect(Collectors.toMap(CatReport::getAdoption, Report::getDate));
        for (CatAdoption adoption:currentCatAdoptionList) {
            if (todayCatReports.containsKey(adoption)) {
                break;
            }else{
                report = catReportRepository.findLatestReport(adoption);
                date = (report != null) ? report.getDate() : adoption.getDate();
                dayLatestReport = ChronoUnit.DAYS.between(date, LocalDate.of(2022, 12,31));
                if(today-dayLatestReport > 2){
                    MessageToVolunteer notification = new MessageToVolunteer();
                    notification.setUser(adoption.getUser());
                    notification.setQuestionTime(LocalDateTime.now());
                    notification.setQuestion("ВНИМАНИЕ !!! Данный опекун не присылал ежедневный отчет более 2 дней");
                    messageToVolunteerRepository.save(notification);
                }else {
                    telegramBot.sendMessageToUser(adoption.getUser(), "ВНИМАНИЕ !!! " +
                            "Просим вас присылать ежедневный отчет до 21:00.", 1000000);
                }
            }
        }

    }

    @Scheduled(cron = "0 0 23 * * *")
    private void sendCongratulation() throws TelegramApiException {
        List<DogAdoption> currentDogAdoptionList = dogAdoptionRepository.findByTrialDateGreaterThanEqual(LocalDate.now());
        for (DogAdoption adoption : currentDogAdoptionList) {
            if (adoption.getTrialDate().isEqual(LocalDate.now())) {
                telegramBot.sendMessageToUser(adoption.getUser(), "Поздравляем !!! Вы успешно прошли " +
                        "испытательный период. Всего наилучшего Вам и вашему питомцу.", 1000000);
            }
        }
        List<CatAdoption> currentCatAdoptionList = catAdoptionRepository.findByTrialDateGreaterThanEqual(LocalDate.now());
        for (CatAdoption adoption : currentCatAdoptionList) {
            if (adoption.getTrialDate().isEqual(LocalDate.now())) {
                telegramBot.sendMessageToUser(adoption.getUser(), "Поздравляем !!! Вы успешно прошли " +
                        "испытательный период. Всего наилучшего Вам и вашему питомцу.", 1000000);
            }
        }
    }

}

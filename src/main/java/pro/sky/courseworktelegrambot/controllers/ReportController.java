package pro.sky.courseworktelegrambot.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.courseworktelegrambot.entities.Report;
import pro.sky.courseworktelegrambot.entities.ShelterId;
import pro.sky.courseworktelegrambot.services.ReportService;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("{shelter_id}/{report_id}")
    public ResponseEntity<Report> getReport(
            @PathVariable("shelter_id") ShelterId shelterId,
            @PathVariable("report_id") Integer reportId) {
        Report dogReport = reportService.getReportById(shelterId, reportId);
        return ResponseEntity.ok(dogReport);
    }

    @DeleteMapping("{shelter_id}/{report_id}")
    public ResponseEntity<Report> deleteReport(
            @PathVariable("shelter_id") ShelterId shelterId,
            @PathVariable("report_id") Integer reportId) {
        Report report = reportService.deleteReportById(shelterId, reportId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("{shelter_id}/all") // все отчеты приюта
    public ResponseEntity<Collection<Report>> getAllReports(ShelterId shelterId) {
        return ResponseEntity.ok(reportService.getAllReports(shelterId));
    }
    @GetMapping(value = "{shelter_id}", params = "date") // отчет за указанную дату
    public  ResponseEntity<Collection<Report>> getReportsByDate(
            @PathVariable("shelter_id") ShelterId shelterId,
            @RequestParam("date") LocalDate date){
            //@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate date){
        return ResponseEntity.ok(reportService.getAllReportsByDate(shelterId, date));
    }
}

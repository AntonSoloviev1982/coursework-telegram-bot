package pro.sky.courseworktelegrambot.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.courseworktelegrambot.entities.Dog;
import pro.sky.courseworktelegrambot.entities.DogReport;
import pro.sky.courseworktelegrambot.services.DogReportService;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("dog-report")
public class DogReportController {
    private final DogReportService dogReportService;

    public DogReportController(DogReportService dogReportService) {
        this.dogReportService = dogReportService;
    }

    @PostMapping
    public ResponseEntity<DogReport> createDogReport(@RequestBody DogReport dogReport) {
        if (dogReport == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dogReportService.createDogReport(dogReport));
    }

    @GetMapping("{reportId}")
    public ResponseEntity<DogReport> getDogReport(@PathVariable Integer reportId) {
        DogReport dogReport = dogReportService.getDogReport(reportId);
        if (dogReport == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dogReport);
    }

    @PutMapping
    public ResponseEntity<DogReport> updateDogReport(@RequestBody DogReport dogReport) {
        DogReport updateDogReport = dogReportService.updateDogReport(dogReport);
        if (updateDogReport == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(updateDogReport);
    }

    @DeleteMapping("{reportId}")
    public ResponseEntity<Dog> deleteDogReport(@PathVariable Integer reportId) {
        dogReportService.deleteDogReport(reportId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("all") // все отчеты
    public ResponseEntity<Collection> getAllDogReports() {
        return ResponseEntity.ok(dogReportService.getAllDogReports());
    }
    @GetMapping("/date") // отчет за указанную дату
    public  ResponseEntity<Collection> getReportByDate(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate date){
        return ResponseEntity.ok(dogReportService.readByDate(date));
    }
}

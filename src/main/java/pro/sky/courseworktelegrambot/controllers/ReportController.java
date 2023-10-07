package pro.sky.courseworktelegrambot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
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

    @Operation(summary = "Поиск отчета о животном по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный отчет о животном",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            })
    @GetMapping("{shelter_id}/{report_id}")
    public ResponseEntity<Report> getReport(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор отчета о животном")
            @PathVariable("report_id") Integer reportId) {
        Report dogReport = reportService.getReportById(shelterId, reportId);
        return ResponseEntity.ok(dogReport);
    }

    @Operation(summary = "Удаление отчета о животном по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаленный отчет о животном",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Report.class)
                            )
                    )
            })
    @DeleteMapping("{shelter_id}/{report_id}")
    public ResponseEntity<Report> deleteReport(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор отчета о животном")
            @PathVariable("report_id") Integer reportId) {
        Report report = reportService.deleteReportById(shelterId, reportId);
        return ResponseEntity.ok(report);
    }

    @Operation(summary = "Поиск всех отчетов о животных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все отчеты о животных",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            })
    @GetMapping("{shelter_id}/all") // все отчеты приюта
    public ResponseEntity<Collection<Report>> getAllReports(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId
    ) {
        return ResponseEntity.ok(reportService.getAllReports(shelterId));
    }

    @Operation(summary = "Поиск всех отчетов о животных на конкретную дату",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все отчеты о животных на конкретную дату",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            })
    @GetMapping(value = "{shelter_id}", params = "date") // отчет за указанную дату
    public  ResponseEntity<Collection<Report>> getReportsByDate(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Дата поиска отчетов о животных")
            @RequestParam("date") LocalDate date){
            //@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate date){
        return ResponseEntity.ok(reportService.getAllReportsByDate(shelterId, date));
    }
}

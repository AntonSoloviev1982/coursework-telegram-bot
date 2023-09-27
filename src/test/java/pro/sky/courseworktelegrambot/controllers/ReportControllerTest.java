package pro.sky.courseworktelegrambot.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.repositories.CatAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.CatReportRepository;
import pro.sky.courseworktelegrambot.repositories.DogAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.DogReportRepository;
import pro.sky.courseworktelegrambot.services.ReportService;
import pro.sky.courseworktelegrambot.services.ShelterService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = ReportController.class)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private DogReportRepository dogReportRepository;

    @Mock
    private CatReportRepository catReportRepository;

    @Mock
    private DogAdoptionRepository dogAdoptionRepository;

    @Mock
    private CatAdoptionRepository catAdoptionRepository;

    @Mock
    private ShelterService shelterService;

    @SpyBean
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;
    @Autowired
    private ObjectMapper objectMapper;

    private DogAdoption adoption;
    private User user;
    private Dog pet;
    private LocalDate trialDate;
    private DogReport report;
    private String shelterId;


    @BeforeEach
    public void beforeEach() {
        ShelterId shelterId = ShelterId.DOG;
        user = new User();
        user.setId(1L);
        user.setName("Ivan");
        user.setShelterId(shelterId);
        pet = new Dog();
        trialDate = LocalDate.now();
        adoption = new DogAdoption(user, pet, trialDate);
        report = new DogReport(adoption, LocalDate.now(), null, null);
        report.setId(1);
    }

    @Test
    public void getReportTest() throws Exception {
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogReportRepository.findById(any())).thenReturn(Optional.of(report));

        mockMvc.perform(
                get("report/Dog/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(result -> {
                    DogReport dogReport = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            DogReport.class
                    );
                    assertThat(dogReport).isEqualTo(report);
                });
        verify(dogReportRepository, atLeast(1)).findById(any());
    }

    @Test
    public void deleteReport() throws Exception {
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogReportRepository.findById(any())).thenReturn(Optional.of(report));

        mockMvc.perform(
                delete("report/Dog/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(result -> {
                    DogReport dogReport = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            DogReport.class
                    );
                    assertThat(dogReport).isEqualTo(report);
                });
        verify(dogReportRepository, atLeast(1)).deleteById(any());
    }

    @Test
    public void getAllReportsTest() throws Exception {
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        List<DogReport> dogReportList = List.of(report);
        when(dogReportRepository.findAll()).thenReturn(dogReportList);

        mockMvc.perform(
                get("report/Dog/all")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(result -> {
                    List<DogReport> dogReports = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<List<DogReport>>() {
                            }
                    );
                    assertThat(dogReports).isNotNull().isNotEmpty();
                    assertThat(dogReports.size()).isEqualTo(dogReportList.size());
                });
    }

    @Test
    public void getReportsByDate() throws Exception {
        LocalDate date = LocalDate.of(2023, 9, 27);
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        List<DogReport> dogReportList = List.of(report);
        when(dogReportRepository.findByDate(date)).thenReturn(dogReportList);

        mockMvc.perform(
                get("report/Dog/?date={2022, 9, 27}")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(result -> {
                    List<DogReport> dogReports = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<List<DogReport>>() {
                            }
                    );
                    assertThat(dogReports).isNotNull().isNotEmpty();
                    assertThat(dogReports.size()).isEqualTo(dogReportList.size());
                });
    }

}

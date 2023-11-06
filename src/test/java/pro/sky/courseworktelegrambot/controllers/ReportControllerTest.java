package pro.sky.courseworktelegrambot.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.repositories.CatAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.CatReportRepository;
import pro.sky.courseworktelegrambot.repositories.DogAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.DogReportRepository;
import pro.sky.courseworktelegrambot.repositories.UserRepository;
import pro.sky.courseworktelegrambot.services.ReportService;
import pro.sky.courseworktelegrambot.services.ShelterService;
import pro.sky.courseworktelegrambot.services.TelegramBotSender;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReportController.class)
public class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DogReportRepository dogReportRepository;

    @MockBean
    private CatReportRepository catReportRepository;

    @MockBean
    private DogAdoptionRepository dogAdoptionRepository;

    @MockBean
    private CatAdoptionRepository catAdoptionRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ShelterService shelterService;

    @MockBean
    private TelegramBotSender telegramBotSender;

    @SpyBean
    private ReportService reportService;

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
        trialDate = LocalDate.of(2023, 9, 20);
        adoption = new DogAdoption(user, pet, trialDate);
        report = new DogReport(adoption, LocalDate.of(2023, 9, 27), null,"image/jpeg", 111, null);
        report.setId(1);
    }

    @Test
    public void getReportTest() throws Exception {
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogReportRepository.findById(any())).thenReturn(Optional.of(report));

        mockMvc.perform(
                get("/report/DOG/1")
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
                delete("/report/DOG/1")
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
                get("/report/DOG/all")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(result -> {
                    List<DogReport> dogReports = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<List<DogReport>>() {
                            }
                    );
                    assertThat(dogReports).isNotNull().isNotEmpty();
                    assertThat(dogReports.get(0).getDate()).isEqualTo(LocalDate.of(2023, 9, 27));
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
                get("/report/DOG/?date=27.09.2023", date)
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

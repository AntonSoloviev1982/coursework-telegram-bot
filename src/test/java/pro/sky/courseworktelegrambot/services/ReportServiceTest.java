package pro.sky.courseworktelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.repositories.CatAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.CatReportRepository;
import pro.sky.courseworktelegrambot.repositories.DogAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.DogReportRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
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

    @InjectMocks
    private ReportService reportService;

    private DogAdoption adoption;
    private User user;
    private Dog pet;
    private LocalDate trialDate;
    private DogReport report;
    private String shelterId;


    @BeforeEach
    public void beforeEach() {
        shelterId = "Dog";
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
    public void saveReportTest() {
        LocalDate date = LocalDate.now();
        List<DogAdoption> adoptionList = new ArrayList<>();
        adoptionList.add(adoption);
        when(dogAdoptionRepository
                .findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        user, LocalDate.now(), LocalDate.now())).thenReturn(adoptionList);
        List<DogReport> reportList = new ArrayList<>();
        reportList.add(report);
        when(dogReportRepository.findByAdoptionAndDate(adoption, date)).thenReturn(reportList);
        when(dogReportRepository.save(report)).thenReturn(report);
        assertThat(reportService.saveReport(user, null, null)).isEqualTo(report);
        verify(dogReportRepository, atLeast(1)).save(report);
    }

    @Test
    public void getReportByIdTest() {
        Mockito.doNothing().when(shelterService).checkShelterId(shelterId);
        when(dogReportRepository.findById(any())).thenReturn(Optional.of(report));
        assertThat(reportService.getReportById(shelterId, 1)).isEqualTo(report);
        verify(dogReportRepository, atLeast(1)).findById(any());
    }

    @Test
    public void deleteReportByIdTest() {
        Mockito.doNothing().when(shelterService).checkShelterId(shelterId);
        when(dogReportRepository.findById(any())).thenReturn(Optional.of(report));
        reportService.deleteReportById(shelterId, 1);
        verify(dogReportRepository, atLeast(1)).deleteById(any());
    }

    @Test
    public void getAllReportsByDateTest() {
        LocalDate date = LocalDate.now();
        Mockito.doNothing().when(shelterService).checkShelterId(shelterId);
        List<DogReport> dogReportList = List.of(report);
        when(dogReportRepository.findByDate(date)).thenReturn(dogReportList);
        assertThat(reportService.getAllReportsByDate(shelterId, date))
                .isNotNull()
                .isNotEmpty();
        assertThat(reportService.getAllReportsByDate(shelterId, date).size())
                .isEqualTo(dogReportList.size());
        assertThat(reportService.getAllReportsByDate(shelterId, date))
                .containsExactlyInAnyOrder(report);
    }

    @Test
    public void getAllReportsTest() {
        Mockito.doNothing().when(shelterService).checkShelterId(shelterId);
        List<DogReport> dogReportList = List.of(report);
        when(dogReportRepository.findAll()).thenReturn(dogReportList);
        assertThat(reportService.getAllReports(shelterId))
                .isNotNull()
                .isNotEmpty();
        assertThat(reportService.getAllReports(shelterId).size())
                .isEqualTo(dogReportList.size());
        assertThat(reportService.getAllReports(shelterId))
                .containsExactlyInAnyOrder(report);
    }

}

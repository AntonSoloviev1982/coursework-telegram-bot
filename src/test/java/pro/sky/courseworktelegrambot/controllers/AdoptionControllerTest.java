package pro.sky.courseworktelegrambot.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.repositories.*;
import pro.sky.courseworktelegrambot.services.AdoptionService;
import pro.sky.courseworktelegrambot.services.ShelterService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdoptionController.class)
public class AdoptionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private DogRepository dogRepository;

    @MockBean
    private CatRepository catRepository;

    @MockBean
    private ShelterRepository shelterRepository;

    @MockBean
    private DogAdoptionRepository dogAdoptionRepository;

    @MockBean
    private CatAdoptionRepository catAdoptionRepository;

    @SpyBean
    private AdoptionService adoptionService;

    @SpyBean
    private ShelterService shelterService;

    @InjectMocks
    private AdoptionController adoptionController;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    private Dog pet;

    private DogAdoption adoption;

    private LocalDate trialDate;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setId(123L);
        pet = new Dog();
        trialDate = LocalDate.of(2022, 11, 22);
        adoption = new DogAdoption(user, pet, trialDate);
    }

    @Test
    public void createAdoptionTest() throws Exception {
        long userId = 123L;
        int petId = 1;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dogRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(dogAdoptionRepository
                .findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        user, trialDate, LocalDate.now())).thenReturn(new ArrayList<>());
        when(dogAdoptionRepository
                .findByPetAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        pet, trialDate, LocalDate.now())).thenReturn(new ArrayList<>());
        when(dogAdoptionRepository.save(adoption)).thenReturn(adoption);

        mockMvc.perform(
                post("/adoption/Dog/?user_id={123L}&pet_id={1}&trial_date={2022, 11, 22}"
                        , userId, petId, trialDate)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(result -> {
                    DogAdoption dogAdoption = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            DogAdoption.class
                    );
                    assertThat(dogAdoption.getTrialDate()).isEqualTo(adoption.getTrialDate());
                    verify(dogAdoptionRepository, atLeast(1)).save(adoption);
                });

    }

    @Test
    public void getAdoptionTest() throws Exception {
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption));
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        mockMvc.perform(
                get("/adoption/Dog/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(result -> {
                    DogAdoption dogAdoption = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            DogAdoption.class
                    );
                    assertThat(dogAdoption).isEqualTo(adoption);
                });
        verify(dogAdoptionRepository, atLeast(1)).findById(any());
    }

    @Test
    public void setTrialDateTest() throws Exception {
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption));
        adoption.setTrialDate(trialDate);
        when(dogAdoptionRepository.save(adoption)).thenReturn(adoption);

        mockMvc.perform(
                put("/adoption/Dog/1/?trial_date={2022, 11, 22}", trialDate)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(result -> {
                    DogAdoption dogAdoption = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            DogAdoption.class
                    );
                    assertThat(dogAdoption.getTrialDate()).isEqualTo(adoption.getTrialDate());
                });
    }

    @Test
    public void deleteAdoption() throws Exception {
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption));
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        mockMvc.perform(
                delete("/adoption/Dog/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(result -> {
                    DogAdoption dogAdoption = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            DogAdoption.class
                    );
                });
        verify(dogAdoptionRepository, new Times(1)).deleteById(any());
        Mockito.reset(dogAdoptionRepository);
    }

    @Test
    public void getAllAdoptionsTest() throws Exception {
        List<DogAdoption> adoptions = new ArrayList<>();
        adoptions.add(adoption);
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogAdoptionRepository.findAll()).thenReturn(adoptions);

        mockMvc.perform(
                get("/adoption/Dog")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(result -> {
                    List<DogAdoption> dogAdoptionList = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<List<DogAdoption>>() {
                            }
                    );
                    assertThat(dogAdoptionList).isNotNull().isNotEmpty();
                    assertThat(dogAdoptionList.size()).isEqualTo(adoptions.size());
                });
    }

}

package pro.sky.courseworktelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.courseworktelegrambot.entities.Dog;
import pro.sky.courseworktelegrambot.entities.DogAdoption;
import pro.sky.courseworktelegrambot.entities.Shelter;
import pro.sky.courseworktelegrambot.entities.User;
import pro.sky.courseworktelegrambot.exceptions.UserIsBusyException;
import pro.sky.courseworktelegrambot.repositories.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdoptionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DogRepository dogRepository;

    @Mock
    private CatRepository catRepository;

    @Mock
    private DogAdoptionRepository dogAdoptionRepository;

    @Mock
    private CatAdoptionRepository catAdoptionRepository;

    @InjectMocks
    private AdoptionService adoptionService;

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
    public void creatAdoptionTest() {
        long userId = 123L;
        int petId = 1;
        String shelterId = "Dog";
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dogRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(dogAdoptionRepository
                .findByUserAndPetAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        user, pet, trialDate, LocalDate.now())).thenReturn(new ArrayList<>());
        when(dogAdoptionRepository.save(adoption)).thenReturn(adoption);

        assertThat(adoptionService.createAdoption(shelterId, userId, petId, trialDate)).isEqualTo(adoption);
        verify(dogAdoptionRepository, atLeast(1)).save(adoption);
    }

    @Test
    public void creatAdoptionNegativeTest() {
        long userId = 123L;
        int petId = 1;
        String shelterId = "Dog";
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(dogRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(dogAdoptionRepository
                .findByUserAndPetAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        user, pet, trialDate, LocalDate.now())).thenReturn(List.of(adoption));

        assertThatExceptionOfType(UserIsBusyException.class)
                .isThrownBy(() -> adoptionService.createAdoption(shelterId, userId, petId, trialDate));
        verify(dogAdoptionRepository, atLeast(0)).save(adoption);
    }

    @Test
    public void getAdoptionTest() {
        int adoptionId = 1;
        String shelterId = "Dog";
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption));

        assertThat(adoptionService.getAdoption(shelterId, adoptionId)).isEqualTo(adoption);
        verify(dogAdoptionRepository, atLeast(1)).findById(adoptionId);
    }

    @Test
    public void getAdoptionNegativeTest() {
        int adoptionId = 1;
        String shelterId = "Dog";
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> adoptionService.getAdoption(shelterId, adoptionId));
        verify(dogAdoptionRepository, atLeast(0)).findById(adoptionId);
    }

    @Test
    public void setTrialDateTest() {
        int adoptionId = 1;
        String shelterId = "Dog";
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption));
        adoption.setTrialDate(trialDate);
        when(dogAdoptionRepository.save(any())).thenReturn(adoption);

        assertThat(adoptionService.setTrialDate(shelterId, adoptionId, trialDate)).isEqualTo(adoption);
        verify(dogAdoptionRepository, atLeast(1)).save(adoption);
    }

    @Test
    public void setTrialDateNegativeTest() {
        int adoptionId = 1;
        String shelterId = "Dog";
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> adoptionService.setTrialDate(shelterId, adoptionId, trialDate));
        verify(dogAdoptionRepository, atLeast(0)).save(adoption);
    }

    @Test
    public void deleteAdoptionTest() {
        int adoptionId = 1;
        String shelterId = "Dog";
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption));

        assertThat(adoptionService.deleteAdoption(shelterId, adoptionId)).isEqualTo(adoption);
        verify(dogAdoptionRepository, atLeast(1)).deleteById(adoptionId);
    }

    @Test
    public void getAllAdoptionsTest() {
        String shelterId = "Dog";
        List<DogAdoption> dogAdoptionList = new ArrayList<>();
        dogAdoptionList.add(adoption);
        when(dogAdoptionRepository.findAll()).thenReturn(dogAdoptionList);
        assertThat(adoptionService.getAllAdoptions(shelterId))
                .hasSize(1)
                .containsExactlyInAnyOrder(adoption);
    }









}

package pro.sky.courseworktelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.exceptions.UserOrPetIsBusyException;
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

    @Mock
    private ShelterService shelterService;

    @InjectMocks
    private AdoptionService adoptionService;

    private User user1;

    private User user2;

    private Dog pet1;

    private Cat pet2;

    private DogAdoption adoption1;

    private CatAdoption adoption2;

    private LocalDate trialDate;

    private ShelterId shelterIdDog;

    private ShelterId shelterIdCat;


    @BeforeEach
    public void beforeEach() {
        shelterIdDog = ShelterId.DOG;
        shelterIdCat = ShelterId.CAT;
        user1 = new User();
        user1.setId(123L);
        user2 = new User();
        user2.setId(125L);
        pet1 = new Dog();
        pet2 = new Cat();
        trialDate = LocalDate.of(2022, 11, 22);
        adoption1 = new DogAdoption(user1, pet1, trialDate);
        adoption2 = new CatAdoption(user2, pet2, trialDate);
    }

    @Test
    public void creatDogAdoptionTest() {
        long userId = 123L;
        int petId = 1;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(dogRepository.findById(petId)).thenReturn(Optional.of(pet1));
        when(dogAdoptionRepository
                .findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        user1, trialDate, LocalDate.now())).thenReturn(new ArrayList<>());
        when(dogAdoptionRepository.save(adoption1)).thenReturn(adoption1);

        assertThat(adoptionService.createAdoption(shelterIdDog, userId, petId, trialDate)).isEqualTo(adoption1);
        verify(dogAdoptionRepository, atLeast(1)).save(adoption1);
    }

    @Test
    public void creatCatAdoptionTest() {
        long userId = 125L;
        int petId = 2;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(catRepository.findById(petId)).thenReturn(Optional.of(pet2));
        when(catAdoptionRepository
                .findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        user2, trialDate, LocalDate.now())).thenReturn(new ArrayList<>());
        when(catAdoptionRepository.save(adoption2)).thenReturn(adoption2);

        assertThat(adoptionService.createAdoption(shelterIdCat, userId, petId, trialDate)).isEqualTo(adoption2);
        verify(catAdoptionRepository, atLeast(1)).save(adoption2);
    }

    @Test
    public void creatAdoptionNegativeUserIsNotFoundTest() {
        long userId = 123L;
        int petId = 1;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> adoptionService.createAdoption(shelterIdDog, userId, petId, trialDate));
        verify(dogAdoptionRepository, atLeast(0)).save(adoption1);
    }

    @Test
    public void creatAdoptionNegativeDogIsNotFoundTest() {
        long userId = 123L;
        int petId = 1;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(dogRepository.findById(petId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> adoptionService.createAdoption(shelterIdDog, userId, petId, trialDate));
        verify(dogAdoptionRepository, atLeast(0)).save(adoption1);
    }

    @Test
    public void creatAdoptionNegativeCatIsNotFoundTest() {
        long userId = 125L;
        int petId = 2;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(catRepository.findById(petId)).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> adoptionService.createAdoption(shelterIdCat, userId, petId, trialDate));
        verify(catAdoptionRepository, atLeast(0)).save(adoption2);
    }

    @Test
    public void creatDogAdoptionFindByUserAndDateLessThanEqualAndTrialDateGreaterThanEqualIsNotEmptyTest() {
        long userId = 123L;
        int petId = 1;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(dogRepository.findById(petId)).thenReturn(Optional.of(pet1));
        when(dogAdoptionRepository
                .findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        user1, trialDate, LocalDate.now())).thenReturn(List.of(adoption1));

        assertThatExceptionOfType(UserOrPetIsBusyException.class)
                .isThrownBy(() -> adoptionService.createAdoption(shelterIdDog, userId, petId, trialDate));
        verify(dogAdoptionRepository, atLeast(0)).save(adoption1);
    }

    @Test
    public void creatDogAdoptionFindByPetAndDateLessThanEqualAndTrialDateGreaterThanEqualIsNotEmptyTest() {
        long userId = 123L;
        int petId = 1;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user1));
        when(dogRepository.findById(petId)).thenReturn(Optional.of(pet1));
        when(dogAdoptionRepository
                .findByPetAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        pet1, trialDate, LocalDate.now())).thenReturn(List.of(adoption1));

        assertThatExceptionOfType(UserOrPetIsBusyException.class)
                .isThrownBy(() -> adoptionService.createAdoption(shelterIdDog, userId, petId, trialDate));
        verify(dogAdoptionRepository, atLeast(0)).save(adoption1);
    }

    @Test
    public void creatCatAdoptionFindByUserAndDateLessThanEqualAndTrialDateGreaterThanEqualIsNotEmptyTest() {
        long userId = 125L;
        int petId = 2;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(catRepository.findById(petId)).thenReturn(Optional.of(pet2));
        when(catAdoptionRepository
                .findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        user2, trialDate, LocalDate.now())).thenReturn(List.of(adoption2));

        assertThatExceptionOfType(UserOrPetIsBusyException.class)
                .isThrownBy(() -> adoptionService.createAdoption(shelterIdCat, userId, petId, trialDate));
        verify(catAdoptionRepository, atLeast(0)).save(adoption2);
    }

    @Test
    public void creatCatAdoptionFindByPetAndDateLessThanEqualAndTrialDateGreaterThanEqualIsNotEmptyTest() {
        long userId = 125L;
        int petId = 2;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user2));
        when(catRepository.findById(petId)).thenReturn(Optional.of(pet2));
        when(catAdoptionRepository
                .findByPetAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                        pet2, trialDate, LocalDate.now())).thenReturn(List.of(adoption2));

        assertThatExceptionOfType(UserOrPetIsBusyException.class)
                .isThrownBy(() -> adoptionService.createAdoption(shelterIdCat, userId, petId, trialDate));
        verify(catAdoptionRepository, atLeast(0)).save(adoption2);
    }

    @Test
    public void getAdoptionTest() {
        int adoptionId = 1;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption1));

        assertThat(adoptionService.getAdoption(shelterIdDog, adoptionId)).isEqualTo(adoption1);
        verify(dogAdoptionRepository, atLeast(1)).findById(adoptionId);
    }

    @Test
    public void getAdoptionNegativeTest() {
        int adoptionId = 1;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> adoptionService.getAdoption(shelterIdDog, adoptionId));
        verify(dogAdoptionRepository, atLeast(0)).findById(adoptionId);
    }

    @Test
    public void setTrialDateDogAdoptionTest() {
        int adoptionId = 1;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption1));
        adoption1.setTrialDate(trialDate);
        when(dogAdoptionRepository.save(any())).thenReturn(adoption1);

        assertThat(adoptionService.setTrialDate(shelterIdDog, adoptionId, trialDate)).isEqualTo(adoption1);
        verify(dogAdoptionRepository, atLeast(1)).save(adoption1);
    }

    @Test
    public void setTrialDateCatAdoptionTest() {
        int adoptionId = 2;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        when(catAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption2));
        adoption2.setTrialDate(trialDate);
        when(catAdoptionRepository.save(any())).thenReturn(adoption2);

        assertThat(adoptionService.setTrialDate(shelterIdCat, adoptionId, trialDate)).isEqualTo(adoption2);
        verify(catAdoptionRepository, atLeast(1)).save(adoption2);
    }

    @Test
    public void setTrialDateNegativeTest() {
        int adoptionId = 1;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> adoptionService.setTrialDate(shelterIdDog, adoptionId, trialDate));
        verify(dogAdoptionRepository, atLeast(0)).save(adoption1);
    }

    @Test
    public void deleteAdoptionTest() {
        int adoptionId = 1;
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogAdoptionRepository.findById(any())).thenReturn(Optional.of(adoption1));

        assertThat(adoptionService.deleteAdoption(shelterIdDog, adoptionId)).isEqualTo(adoption1);
        verify(dogAdoptionRepository, atLeast(1)).deleteById(adoptionId);
    }

    @Test
    public void getAllDogAdoptionsTest() {
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        List<DogAdoption> dogAdoptionList = new ArrayList<>();
        dogAdoptionList.add(adoption1);
        when(dogAdoptionRepository.findAll()).thenReturn(dogAdoptionList);
        assertThat(adoptionService.getAllAdoptions(shelterIdDog))
                .hasSize(1)
                .containsExactlyInAnyOrder(adoption1);
    }

    @Test
    public void getAllCatAdoptionsTest() {
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        List<CatAdoption> catAdoptionList = new ArrayList<>();
        catAdoptionList.add(adoption2);
        when(catAdoptionRepository.findAll()).thenReturn(catAdoptionList);
        assertThat(adoptionService.getAllAdoptions(shelterIdCat))
                .hasSize(1)
                .containsExactlyInAnyOrder(adoption2);
    }

    @Test
    public void getAllActiveDogAdoptionsTest() {
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        List<DogAdoption> dogAdoptionList = new ArrayList<>();
        dogAdoptionList.add(adoption1);
        when(dogAdoptionRepository
                .findByDateLessThanEqualAndTrialDateGreaterThanEqual(LocalDate.now(), LocalDate.now()))
                .thenReturn(dogAdoptionList);
        assertThat(adoptionService.getAllActiveAdoptions(shelterIdDog))
                .hasSize(1)
                .containsExactlyInAnyOrder(adoption1);
    }

    @Test
    public void getAllActiveCatAdoptionsTest() {
        Mockito.doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        List<CatAdoption> catAdoptionList = new ArrayList<>();
        catAdoptionList.add(adoption2);
        when(catAdoptionRepository
                .findByDateLessThanEqualAndTrialDateGreaterThanEqual(LocalDate.now(), LocalDate.now()))
                .thenReturn(catAdoptionList);
        assertThat(adoptionService.getAllActiveAdoptions(shelterIdCat))
                .hasSize(1)
                .containsExactlyInAnyOrder(adoption2);
    }









}

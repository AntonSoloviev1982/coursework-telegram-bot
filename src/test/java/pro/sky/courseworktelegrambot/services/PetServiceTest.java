package pro.sky.courseworktelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.courseworktelegrambot.entities.Cat;
import pro.sky.courseworktelegrambot.entities.Dog;
import pro.sky.courseworktelegrambot.entities.Pet;
import pro.sky.courseworktelegrambot.entities.ShelterId;
import pro.sky.courseworktelegrambot.repositories.CatRepository;
import pro.sky.courseworktelegrambot.repositories.DogRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {
    @Mock
    private DogRepository dogRepository;

    @Mock
    private CatRepository catRepository;

    @Mock
    private ShelterService shelterService;

    @InjectMocks
    private PetService petService;

    private final Dog dog = new Dog();
    private final Cat cat = new Cat();

    @BeforeEach
    void setUp() {
        dog.setId(1);
        dog.setName("Тузик");
        cat.setId(2);
        cat.setName("Кокос");
    }

    @Test
    void createDogPet() {
        doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogRepository.save(any(Dog.class))).thenReturn(dog);
        Pet createdPet = petService.createPet(ShelterId.DOG, dog);

        assertNotNull(createdPet);
        assertEquals(dog.getId(), createdPet.getId());
        assertEquals(dog.getName(), createdPet.getName());
        verify(dogRepository).save(any(Dog.class));
    }

    @Test
    void createCatPet() {
        doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        when(catRepository.save(any(Cat.class))).thenReturn(cat);
        Pet createdPet = petService.createPet(ShelterId.CAT, cat);

        assertNotNull(createdPet);
        assertEquals(cat.getId(), createdPet.getId());
        assertEquals(cat.getName(), createdPet.getName());
        verify(catRepository).save(any(Cat.class));
    }

    @Test
    void getDogPet() {
        doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogRepository.findById(dog.getId())).thenReturn(Optional.of(dog));
        Pet existingPet = petService.getPet(ShelterId.DOG, dog.getId());

        assertNotNull(existingPet);
        assertEquals(dog.getId(), existingPet.getId());
        assertEquals(dog.getName(), existingPet.getName());
        verify(dogRepository).findById(dog.getId());
    }

    @Test
    void getCatPet() {
        doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        when(catRepository.findById(cat.getId())).thenReturn(Optional.of(cat));
        Pet existingPet = petService.getPet(ShelterId.CAT, cat.getId());

        assertNotNull(existingPet);
        assertEquals(cat.getId(), existingPet.getId());
        assertEquals(cat.getName(), existingPet.getName());
        verify(catRepository).findById(cat.getId());
    }

    @Test
    void getNonExistingPetShouldThrowNotFoundException() {
        doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        when(catRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> petService.getPet(ShelterId.CAT, 3));
    }

    @Test
    void updateDogPet() {
        Dog updatedDog = new Dog();
        updatedDog.setId(1);
        updatedDog.setName("Updated Doggo");
        doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogRepository.findById(dog.getId())).thenReturn(Optional.of(dog));
        when(dogRepository.save(any(Dog.class))).thenReturn(updatedDog);

        Pet updatedPet = petService.updatePet(ShelterId.DOG, updatedDog);
        assertNotNull(updatedPet);
        assertEquals(updatedDog.getId(), updatedPet.getId());
        assertEquals(updatedDog.getName(), updatedPet.getName());
        verify(dogRepository).save(any(Dog.class));
    }

    @Test
    void updateCatPet() {
        Cat updatedCat = new Cat();
        updatedCat.setId(2);
        updatedCat.setName("Updated Catto");
        doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        when(catRepository.findById(cat.getId())).thenReturn(Optional.of(cat));
        when(catRepository.save(any(Cat.class))).thenReturn(updatedCat);

        Pet updatedPet = petService.updatePet(ShelterId.CAT, updatedCat);
        assertNotNull(updatedPet);
        assertEquals(updatedCat.getId(), updatedPet.getId());
        assertEquals(updatedCat.getName(), updatedPet.getName());
        verify(catRepository).save(any(Cat.class));
    }

    @Test
    void deleteDogPet() {
        doNothing().when(shelterService).checkShelterId(ShelterId.DOG);
        when(dogRepository.findById(dog.getId())).thenReturn(Optional.of(dog));
        doNothing().when(dogRepository).deleteById(dog.getId());
        Pet deletedPet = petService.deletePet(ShelterId.DOG, dog.getId());

        assertNotNull(deletedPet);
        assertEquals(dog.getId(), deletedPet.getId());
        assertEquals(dog.getName(), deletedPet.getName());
        verify(dogRepository).deleteById(dog.getId());
    }

    @Test
    void deleteCatPet() {
        doNothing().when(shelterService).checkShelterId(ShelterId.CAT);
        when(catRepository.findById(cat.getId())).thenReturn(Optional.of(cat));
        doNothing().when(catRepository).deleteById(cat.getId());
        Pet deletedPet = petService.deletePet(ShelterId.CAT, cat.getId());

        assertNotNull(deletedPet);
        assertEquals(cat.getId(), deletedPet.getId());
        assertEquals(cat.getName(), deletedPet.getName());
        verify(catRepository).deleteById(cat.getId());
    }
}
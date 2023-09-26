package pro.sky.courseworktelegrambot.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entities.Cat;
import pro.sky.courseworktelegrambot.entities.Dog;
import pro.sky.courseworktelegrambot.entities.Pet;
import pro.sky.courseworktelegrambot.exceptions.UserOrPetIsBusyException;
import pro.sky.courseworktelegrambot.repositories.CatRepository;
import pro.sky.courseworktelegrambot.repositories.DogRepository;
import pro.sky.courseworktelegrambot.repositories.ShelterRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


/**
 * Сервис управления данными питомца
 * В этом сервисе находятся методы удаления, создания, и
 * обновления данных о питомце (собаке или кошки)
 */
@Service
public class PetService {
    private final DogRepository dogRepository;
    private final CatRepository catRepository;
    private final ShelterService shelterService;

    public PetService(DogRepository dogRepository,
                      CatRepository catRepository,
                      ShelterService shelterService) {
        this.dogRepository = dogRepository;
        this.catRepository = catRepository;
        this.shelterService = shelterService;
    }

    private JpaRepository<? extends Pet, Integer> petRepository(String shelterId) {
        return (shelterId.equals("Dog")) ? dogRepository : catRepository;
    }

    /**
     * Метод создает систему усыновления и сохраняет данные в таблицу Adoption
     * Используется метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param shelterId идентификатор приюта.
     * @param pet       данные о питомце
     * @return cat или dog
     */
    public Pet createPet(String shelterId, Pet pet) {
        shelterService.checkShelterId(shelterId);
        if (shelterId.equals("Dog")) {
            Dog dog = new Dog(pet);
            dog.setId(0);  //при создании id не указываем
            return dogRepository.save(dog);
        } else {
            Cat cat = new Cat(pet);
            cat.setId(0);  //при создании id не указываем
            return catRepository.save(cat);
        }
    }
    /**
     * Метод создает систему усыновления и сохраняет данные в таблицу Adoption
     * Используется метод репозитория {@link JpaRepository#findById(Object)}
     *
     * @param shelterId идентификатор приюта.
     * @param id     индификатор питомца который вводит пользователь
     * @throws  EntityNotFoundException
     */
    public Pet getPet(String shelterId, int id) {
        return petRepository(shelterId).findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pet with id " + id + " in shelter " + shelterId + " not found"));
    }
    /**
     * Метод создает систему усыновления и сохраняет данные в таблицу Adoption
     * Используется метод репозитория {@link JpaRepository#save(Object)}
     * @param shelterId идентификатор приюта.
     * @param pet    данные о питомце которые нужно обновить
     * @return  сохраненные данные питомца
     * */
    public Pet updatePet(String shelterId, Pet pet) {
        //Dog dog=(Dog)pet;  //Pet cannot be cast to class Dog
        shelterService.checkShelterId(shelterId);
        if (shelterId.equals("Dog")) {
            return dogRepository.save(new Dog(pet));
        } else {
            return catRepository.save(new Cat(pet));
        }
    }

    /**
     * Метод удаляет питомца из БД
     * Используется метод репозитория {@link JpaRepository#deleteById(Object)}
     * @param shelterId идентификатор приюта.
     * @param id   индификатор питомца который удаляем
     * @return  сохраненные данные питомца
     * */
    public Pet deletePet(String shelterId, int id) {
        Pet pet = getPet(shelterId, id);
        petRepository(shelterId).deleteById(id);
        return pet;
    }

    /**
     * Метод выводит всех питомцев в приюте
     * Используется метод репозитория {@link JpaRepository#findAll()}
     * @param shelterId идентификатор приюта.
     * @return  Список питомцев в приюте
     * */
    public Collection<Pet> getAllPets(String shelterId) {
        return List.copyOf(petRepository(shelterId).findAll());
    }
}


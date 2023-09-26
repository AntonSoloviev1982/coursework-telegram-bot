package pro.sky.courseworktelegrambot.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entities.Cat;
import pro.sky.courseworktelegrambot.entities.Dog;
import pro.sky.courseworktelegrambot.entities.Pet;
import pro.sky.courseworktelegrambot.exceptions.ShelterNotFoundException;
import pro.sky.courseworktelegrambot.repositories.CatRepository;
import pro.sky.courseworktelegrambot.repositories.DogRepository;

import java.util.Collection;
import java.util.List;


/**
 * Сервис управления данными питомца
 * В этом сервисе находятся методы удаления, создания, и
 * обновления данных о питомце (собаке или кошке)
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

    //из такого репозитория удается прочитать, возвращается Pet
    //но в него ничего не удается сохранить
    private JpaRepository<? extends Pet, Integer> petRepository(String shelterId) {
        return (shelterId.equals("Dog")) ? dogRepository : catRepository;
    }

    /**
     * Метод создает нового питомца (собаку или кошку) в БД
     * на основе объекта Pet, получаемого из тела http-запроса.
     * Поле id переданного о объекта игнорируется
     * Используется метод репозитория {@link JpaRepository#save(Object)}
     *
     * @param shelterId идентификатор приюта.
     * @param pet       данные о питомце
     * @return Pet
     * @throws ShelterNotFoundException если id приюта не найден в базе
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
     * Метод возвращает данные о питомце
     * Используется метод репозитория {@link JpaRepository#findById(Object)}
     *
     * @param shelterId идентификатор приюта.
     * @param id        индификатор питомца, данные которого необходимо вернуть
     * @throws ShelterNotFoundException если id приюта не найден в базе
     * @throws EntityNotFoundException если id питомца не найден в базе
     */
    public Pet getPet(String shelterId, int id) {
        shelterService.checkShelterId(shelterId);
        return petRepository(shelterId).findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pet with id " + id + " in shelter " + shelterId + " not found"));
    }
    /**
     * Метод обновляет данные о питомце в БД, на основе переданного объекта Pet
     * Используется метод репозитория {@link JpaRepository#save(Object)}
     * @param shelterId идентификатор приюта.
     * @param pet       данные о питомце которые нужно обновить по его id
     * @return          сохраненные данные питомца
     * @throws ShelterNotFoundException если id приюта не найден в базе
     * @throws EntityNotFoundException если id питомца не найден в базе
     * */
    public Pet updatePet(String shelterId, Pet pet) {
        //Dog dog=(Dog)pet;  //Pet cannot be cast to class Dog. Поэтому вызываем new
        shelterService.checkShelterId(shelterId);
        //если ключ не найден или 0, то save создает новую запись.
        //Поэтому проверим существование id
        getPet(shelterId, pet.getId());  //если id не существует, здесь будет исключение
        if (shelterId.equals("Dog")) {
            return dogRepository.save(new Dog(pet));  //обертываем Pet
        } else {
            return catRepository.save(new Cat(pet));
        }
    }

    /**
     * Метод удаляет питомца из БД
     * Используется метод репозитория {@link JpaRepository#deleteById(Object)}
     * @param shelterId идентификатор приюта.
     * @param id        индификатор питомца, который удаляем
     * @return          данные удаленного питомца
     * @throws ShelterNotFoundException если id приюта не найден в базе
     * @throws EntityNotFoundException если id питомца не найден в базе
     * */
    public Pet deletePet(String shelterId, int id) {
        shelterService.checkShelterId(shelterId);
        Pet pet = getPet(shelterId, id);
        petRepository(shelterId).deleteById(id);
        return pet;
    }

    /**
     * Метод выводит всех питомцев в приюте
     * Используется метод репозитория {@link JpaRepository#findAll()}
     * @param shelterId идентификатор приюта.
     * @return  Список питомцев в приюте
     * @throws ShelterNotFoundException если id приюта не найден в базе
     * */
    public Collection<Pet> getAllPets(String shelterId) {
        shelterService.checkShelterId(shelterId);
        return List.copyOf(petRepository(shelterId).findAll());
    }
}


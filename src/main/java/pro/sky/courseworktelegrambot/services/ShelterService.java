package pro.sky.courseworktelegrambot.services;

import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entities.Shelter;
import pro.sky.courseworktelegrambot.exceptions.InformationTypeByShelterNotFoundException;
import pro.sky.courseworktelegrambot.exceptions.ShelterNotFoundException;
import pro.sky.courseworktelegrambot.repositories.ShelterRepository;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * В классе ShelterService содержится бизнес логика для работы с информацией о приютах.
 */
@Service
public class ShelterService {

    /**
     * Лист для получения всех приютов из БД.
     */
    private final List<Shelter> shelters;

    private final ShelterRepository shelterRepository;


    public ShelterService(ShelterRepository shelterRepository) {
        this.shelterRepository = shelterRepository;
        this.shelters = shelterRepository.findAll();
    }

    /**
     * Создает новый объект Shelter и сохраняет его в БД.
     *
     * @param shelter Объект Shelter для сохранения.
     * @return Возвращает созданный Shelter после сохранения.
     * @throws IllegalArgumentException Если аргумент shelter равен null.
     */
    public Shelter create(Shelter shelter) {
        return shelterRepository.save(shelter);
    }

    /**
     * Извлекает объект Shelter по указанному идентификатору.
     *
     * @param id идентификатор Shelter для извлечения.
     * @return возвращает объект Shelter.
     * @throws ShelterNotFoundException Если Shelter с указанным идентификатором не найдено.
     */
    public Shelter get(String id) {
        Optional<Shelter> optionalShelter = Optional.ofNullable(id)
                .flatMap(shelterRepository::findById);
        return optionalShelter.orElseThrow(() -> new ShelterNotFoundException(id));
    }

    /**
     * Обновляет данные Shelter с указанным идентификатором в БД.
     *
     * @param id              идентификатор Shelter, который нужно обновить.
     * @param informationType тип информации о приюте.
     * @param newInformation  новая информация.
     * @return возвращает обновленный объект Shelter.
     * @throws ShelterNotFoundException если объект Shelter с указанным идентификатором не найден.
     */
    public Shelter update(String id, String informationType, String newInformation) throws IllegalAccessException {
        Shelter shelter = setInformation(id, informationType, newInformation);
        shelterRepository.save(shelter);
        return shelter;
    }

    /**
     * Удаляет объект Shelter с указанным идентификатором из БД.
     *
     * @param id идентификатор объекта Shelter для удаления.
     * @throws ShelterNotFoundException Если объект Shelter с указанным идентификатором не найден.
     */
    public void delete(String id) {
        Optional<Shelter> shelterOptional = shelterRepository.findById(id);
        if (shelterOptional.isPresent()) {
            shelterRepository.deleteById(id);
        } else {
            throw new ShelterNotFoundException(id);
        }
    }

    /**
     * Извлекает список всех объектов Shelter из БД.
     *
     * @return возвращает список всех объектов Shelter.
     */
    public List<Shelter> findAll() {
        return List.copyOf(shelterRepository.findAll());
    }

    /**
     * Получает необходимое значение поля из объекта Shelter, находящегося в листе shelters.
     * @param id                идентификатор объекта Shelter.
     * @param informationType   тип информации о приюте..
     * @return возвращает нужную информацию о приюте.
     * @throws IllegalAccessException выбрасывается если базовое поле не доступно.
     */
    public String getInformation(String id, String informationType)
            throws IllegalAccessException {
        Shelter shelter = null;
        for (Shelter myShelter : shelters) {
            if (myShelter.getId().equals(id)) {
                shelter = myShelter;
            }
        }
        if (shelter == null) {
            throw new ShelterNotFoundException(id);
        }
        Field field = null;
        Field[] fields = shelter.getClass().getDeclaredFields();
        for (Field myField : fields) {
            if (myField.getName().equals(informationType)) {
                field = myField;
            }
        }
        if (field == null) {
            throw new InformationTypeByShelterNotFoundException(informationType);
        }
        field.setAccessible(true);
        return (String) field.get(shelter);
    }

    /**
     * Изменяет значение типа информации (поля) о приюте. Изменения записываются в лист shelters.
     * @param id                идентификатор объекта Shelter.
     * @param informationType   тип информации о приюте..
     * @param newInformation    новая информация.
     * @return возвращает обновленный объект Shelter.
     * @throws IllegalAccessException выбрасывается если базовое поле не доступно.
     */
    public Shelter setInformation(String id, String informationType, String newInformation)
            throws IllegalAccessException {
        Shelter shelter = null;
        for (Shelter myShelter : shelters) {
            if (myShelter.getId().equals(id)) {
                shelter = myShelter;
            }
        }
        if (shelter == null) {
            throw new ShelterNotFoundException(id);
        }
        Field[] fields = shelter.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().equals(informationType)) {
                field.setAccessible(true);
                field.set(shelter, newInformation);
            }
        }
        return shelter;
    }

}

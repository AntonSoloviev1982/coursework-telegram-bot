package pro.sky.courseworktelegrambot.services;

import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entity.Shelter;
import pro.sky.courseworktelegrambot.exceptions.ShelterNotFoundException;
import pro.sky.courseworktelegrambot.repositories.ShelterRepository;

import java.util.List;

@Service
public class ShelterService {

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
    public Shelter get(int id) {
        return shelterRepository.findById(id).orElseThrow(() -> new ShelterNotFoundException(id));
    }

    /**
     * Обновляет данные Shelter с указанным идентификатором.
     *
     * @param id      идентификатор Shelter, которое нужно обновить.
     * @param shelter объект Shelter с обновленными данными.
     * @return возвращает обновленный объект Shelter.
     * @throws ShelterNotFoundException если объект Shelter с указанным идентификатором не найден.
     */
    public Shelter update(int id, Shelter shelter) {
        return shelterRepository.findById(id)
                .map(oldShelter -> {
                    oldShelter.setName(shelter.getName());
                    oldShelter.setInformation(shelter.getInformation());
                    oldShelter.setTimetable(shelter.getTimetable());
                    oldShelter.setAddress(shelter.getAddress());
                    oldShelter.setSecurity(shelter.getSecurity());
                    oldShelter.setSafetyPrecautions(shelter.getSafetyPrecautions());
                    oldShelter.setRules(shelter.getRules());
                    oldShelter.setDocuments(shelter.getDocuments());
                    oldShelter.setTransportation(shelter.getTransportation());
                    oldShelter.setChildAccomodation(shelter.getChildAccomodation());
                    oldShelter.setAdultAccomodation(shelter.getAdultAccomodation());
                    oldShelter.setInvalidAccomodation(shelter.getInvalidAccomodation());
                    oldShelter.setCommunication(shelter.getCommunication());
                    oldShelter.setCynologists(shelter.getCynologists());
                    oldShelter.setRefusalReasons(shelter.getRefusalReasons());
                    return shelterRepository.save(oldShelter);
                }).orElseThrow(() -> new ShelterNotFoundException(id));
    }

    /**
     * Удаляет объект Shelter с указанным идентификатором из БД.
     *
     * @param id идентификатор объекта Shelter для удаления.
     * @return возвращает удаленный объект Shelter.
     * @throws ShelterNotFoundException Если объект Shelter с указанным идентификатором не найден.
     */
    public Shelter delete(int id) {
        Shelter shelter = shelterRepository.findById(id)
                .orElseThrow(() -> new ShelterNotFoundException(id));
        shelterRepository.deleteById(id);
        return shelter;
    }

    /**
     * Извлекает список всех объектов Shelter из БД.
     *
     * @return возвращает список всех объектов Shelter.
     */
    public List<Shelter> findAll() {
        return shelterRepository.findAll();
    }


    public String getProperty(int shelterId, String propertyName) {
        return null;
    }

    public void setProperty(int shelterId, String propertyName) {

    }

}

package pro.sky.courseworktelegrambot.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entity.Dog;
import pro.sky.courseworktelegrambot.entity.DogAdoption;
import pro.sky.courseworktelegrambot.repositories.DogAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.DogRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class DogAdoptionService {
    private final DogAdoptionRepository dogAdoptionRepository;

    public DogAdoptionService(DogAdoptionRepository dogAdoptionRepository) {
        this.dogAdoptionRepository = dogAdoptionRepository;
    }

    public DogAdoption createDogAdoption(DogAdoption dogAdoption) {
        return dogAdoptionRepository.save(dogAdoption);
    }

    public DogAdoption getDogAdoption(Integer id) {
        return dogAdoptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dog with id " + id + " not found"));
    }

    public DogAdoption updateDog(DogAdoption dogAdoptionRepository) {
        DogAdoption dogFromDb = getDogAdoption(dogAdoptionRepository.getId());
        dogFromDb.setName(dogAdoptionRepository.getName());
        dogFromDb.setAge(dogAdoptionRepository.getAge());
        dogFromDb.setBreed(dogAdoptionRepository.getBreed());
        dogFromDb.setPhoto(dogAdoptionRepository.getPhoto());
        return dogAdoptionRepository.save(dogAdoptionRepository);
    }

    public void deleteDog(Integer id) {
        Optional<DogAdoption> dogOptional = dogAdoptionService.findById(id);
        if (dogOptional.isPresent()) {
            dogAdoptionService.deleteById(id);
        } else {
            throw new EntityNotFoundException("Dog with id " + id + " not found");
        }
    }
    public Collection<DogAdoption> getAllDog(){
        return dogAdoptionService.findAll();
    }
}

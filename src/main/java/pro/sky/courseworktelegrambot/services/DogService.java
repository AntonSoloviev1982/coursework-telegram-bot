package pro.sky.courseworktelegrambot.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entity.Dog;
import pro.sky.courseworktelegrambot.repositories.DogRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class DogService {
    private final DogRepository dogRepository;

    public DogService(DogRepository dogRepository) {
        this.dogRepository = dogRepository;
    }

    public Dog createDog(Dog dog) {
        return dogRepository.save(dog);
    }

    public Dog getDog(Integer id) {
        return dogRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dog with id " + id + " not found"));
    }

    public Dog updateDog(Dog dog) {
        Dog dogFromDb = getDog(dog.getId());
        dogFromDb.setName(dog.getName());
        dogFromDb.setAge(dog.getAge());
        dogFromDb.setBreed(dog.getBreed());
        dogFromDb.setPhoto(dog.getPhoto());
        return dogRepository.save(dog);
    }

    public void deleteDog(Integer id) {
        Optional<Dog> dogOptional = dogRepository.findById(id);
        if (dogOptional.isPresent()) {
            dogRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Dog with id " + id + " not found");
        }
    }
    public Collection<Dog> getAllDog(){
        return dogRepository.findAll();
    }
}


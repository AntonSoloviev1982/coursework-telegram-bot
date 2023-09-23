package pro.sky.courseworktelegrambot.services;

import javax.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entities.DogAdoption;
import pro.sky.courseworktelegrambot.repositories.DogAdoptionRepository;
import pro.sky.courseworktelegrambot.repositories.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class DogAdoptionService {
    private final DogAdoptionRepository dogAdoptionRepository;
    private final UserRepository userRepository;

    public DogAdoptionService(DogAdoptionRepository dogAdoptionRepository, UserRepository userRepository) {
        this.dogAdoptionRepository = dogAdoptionRepository;
        this.userRepository = userRepository;
    }

    public DogAdoption createDogAdoption(DogAdoption dogAdoption) {
        if (dogAdoptionRepository.existsByUserIdOrPetId(dogAdoption.getUserId(),dogAdoption.getPetId())){
            return dogAdoption;
        }
        return dogAdoptionRepository.save(dogAdoption);
    }

    public DogAdoption getDogAdoption(Integer id) {
        return dogAdoptionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dog with id " + id + " not found"));
    }

    public DogAdoption updateDogAdoption(DogAdoption dogAdoption) {
        DogAdoption dogAdoptionFromDB = getDogAdoption(dogAdoption.getId());
        dogAdoptionFromDB.setUserId(dogAdoption.getUserId());
        dogAdoptionFromDB.setPetId(dogAdoption.getPetId());
        dogAdoptionFromDB.setDate(dogAdoption.getDate());
        dogAdoptionFromDB.setTrialDate(dogAdoption.getTrialDate());
        dogAdoptionFromDB.setTrialDecision(dogAdoption.getTrialDecision());
        return dogAdoptionRepository.save(dogAdoption);
    }

    public void deleteDogAdoption(Integer id) {
        Optional<DogAdoption> dogOptional = dogAdoptionRepository.findById(id);
        if (dogOptional.isPresent()) {
            dogAdoptionRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Dog with id " + id + " not found");
        }
    }
    public Collection<DogAdoption> getAllDogAdoption(){
        return List.copyOf(dogAdoptionRepository.findAll());
    }
}

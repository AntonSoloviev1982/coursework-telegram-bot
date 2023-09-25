package pro.sky.courseworktelegrambot.services;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pro.sky.courseworktelegrambot.entities.*;
import pro.sky.courseworktelegrambot.exceptions.UserOrPetIsBusyException;
import pro.sky.courseworktelegrambot.repositories.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Service
public class AdoptionService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DogRepository dogRepository;
    @Autowired
    private CatRepository catRepository;

    @Autowired
    private DogAdoptionRepository dogAdoptionRepository;
    @Autowired
    private CatAdoptionRepository catAdoptionRepository;
    @Autowired
    private ShelterService shelterService;

    //из такого репозитория удается прочитать, возвращается предок
    //но в него ничего не удается сохранить уже при компиляции, при подстановке любого типа возникает
    //method save in interface org.springframework.data.repository.CrudRepository<T,ID> cannot be applied to given types;
    private JpaRepository<? extends Adoption, Integer> adoptionRepository(String shelterId) {
        return (shelterId.equals("Dog")) ? dogAdoptionRepository : catAdoptionRepository;
    }

    public Adoption createAdoption(String shelterId, long userId, int petId, LocalDate trialDate) {
        shelterService.checkShelterId(shelterId);
        //Проверяем, что заданный User есть
        User user = userRepository.findById(userId).orElseThrow();

        if (shelterId.equals("Dog")) {
            //Проверяем, что заданный Dog есть
            //Dog pet = dogRepository.getReferenceById(petId);  //так не идет обращение к  БД. Оно будет позже
            Dog pet = dogRepository.findById(petId).orElseThrow();
            //Проверяем, что у пользователя нет другого испытательного срока
            if (!dogAdoptionRepository.findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                    user, trialDate, LocalDate.now()).isEmpty()){
                throw new UserOrPetIsBusyException();
            }
            //Проверяем, что у собаки нет другого испытательного срока
            if (!dogAdoptionRepository.findByPetAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                    pet, trialDate, LocalDate.now()).isEmpty()){
                throw new UserOrPetIsBusyException();
            }
            DogAdoption adoption=new DogAdoption(user, pet, trialDate);
            return dogAdoptionRepository.save(adoption);
        } else {
            Cat pet = catRepository.findById(petId).orElseThrow();
            if (!catAdoptionRepository.findByUserAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                    user, trialDate, LocalDate.now()).isEmpty()){
                throw new UserOrPetIsBusyException();
            }
            if (!catAdoptionRepository.findByPetAndDateLessThanEqualAndTrialDateGreaterThanEqual(
                    pet, trialDate, LocalDate.now()).isEmpty()){
                throw new UserOrPetIsBusyException();
            }
            CatAdoption adoption=new CatAdoption(user, pet, trialDate);
            return catAdoptionRepository.save(adoption);
        }
    }

    public Adoption getAdoption(String shelterId, int adoptionId) {
        shelterService.checkShelterId(shelterId);
        Adoption adoption = adoptionRepository(shelterId).findById(adoptionId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Adoption with id " + adoptionId + " for shelter " + shelterId + " not found"));
        //System.out.println(adoption.getClass());
        return adoption;
    }

    public Adoption setTrialDate(String shelterId, Integer adoptionId, LocalDate trialDate) {
        shelterService.checkShelterId(shelterId);
        if (shelterId.equals("Dog")) {
            DogAdoption adoption = dogAdoptionRepository.findById(adoptionId).orElseThrow(() -> new EntityNotFoundException(
                    "Adoption with id " + adoptionId + " for shelter " + shelterId + " not found"));
            adoption.setTrialDate(trialDate);
            return dogAdoptionRepository.save(adoption);
        } else {
            CatAdoption adoption = catAdoptionRepository.findById(adoptionId).orElseThrow(() -> new EntityNotFoundException(
                    "Adoption with id " + adoptionId + " for shelter " + shelterId + " not found"));
            adoption.setTrialDate(trialDate);
            return catAdoptionRepository.save(adoption);
        }
    }

    public Adoption deleteAdoption(String shelterId, int adoptionId) {
        shelterService.checkShelterId(shelterId);
        Adoption adoption=getAdoption(shelterId, adoptionId);
        adoptionRepository(shelterId).deleteById(adoptionId);
        return adoption;
    }
    public Collection<Adoption> getAllAdoptions(String shelterId){
        shelterService.checkShelterId(shelterId);
        return List.copyOf(adoptionRepository(shelterId).findAll());
    }
    public Collection<Adoption> getAllActiveAdoptions(String shelterId){
        shelterService.checkShelterId(shelterId);
        if (shelterId.equals("Dog")) {
            return List.copyOf(dogAdoptionRepository
                    .findByDateLessThanEqualAndTrialDateGreaterThanEqual(LocalDate.now(), LocalDate.now()));
        } else {
            return List.copyOf(catAdoptionRepository
                    .findByDateLessThanEqualAndTrialDateGreaterThanEqual(LocalDate.now(), LocalDate.now()));
        }
    }
}

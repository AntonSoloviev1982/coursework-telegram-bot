package pro.sky.courseworktelegrambot.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.courseworktelegrambot.entities.Adoption;
import pro.sky.courseworktelegrambot.entities.DogAdoption;
import pro.sky.courseworktelegrambot.services.AdoptionService;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("adoption")
public class AdoptionController {

    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    @PostMapping("{shelter_id}")
    public ResponseEntity<Adoption> createAdoption(
            @PathVariable(name="shelter_id") String shelterId,
            @RequestParam("user_id") long userId,
            @RequestParam("pet_id") int petId,
            //date возьмем сегодня
            @RequestParam("trial_date") LocalDate trialDate
            ) {

        return ResponseEntity.ok(
                adoptionService.createAdoption(shelterId, userId, petId, trialDate));
        //можно и так тоже
        //return adoptionService.createAdoption(shelterId, userId, petId, trialDate);
    }

    @GetMapping("{shelter_id}/{adoption_id}")
    public Adoption getAdoption(
            @PathVariable("shelter_id") String shelterId,
            @PathVariable("adoption_id") Integer adoptionId) {
        return adoptionService.getAdoption(shelterId, adoptionId);
      }

    @PutMapping(value = "{shelter_id}/{adoption_id}", params="trial_date")
    public Adoption setTrialDate(
            @PathVariable("shelter_id") String shelterId,
            @PathVariable("adoption_id") Integer adoptionId,
            @RequestParam("trial_date") LocalDate trialDate
            ) {
        return adoptionService.setTrialDate(shelterId, adoptionId, trialDate);
    }

    @DeleteMapping("{shelter_id}/{adoption_id}")
    public Adoption deleteAdoption(
            @PathVariable("shelter_id") String shelterId,
            @PathVariable("adoption_id") Integer adoptionId) {
        return adoptionService.deleteAdoption(shelterId, adoptionId);
    }

    @GetMapping("{shelter_id}")
    public Collection<Adoption> getAllAdoptions(String shelterId) {
        return adoptionService.getAllAdoptions(shelterId);
    }
    @GetMapping("{shelter_id}/active")
    public Collection<Adoption> getAllActiveAdoptions(String shelterId) {
        return adoptionService.getAllActiveAdoptions(shelterId);
    }
}

package pro.sky.courseworktelegrambot.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.courseworktelegrambot.entities.Pet;
import pro.sky.courseworktelegrambot.entities.ShelterId;
import pro.sky.courseworktelegrambot.services.PetService;

import java.util.Collection;

@RestController
@RequestMapping("pet")
public class PetController {
    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping("{shelter_id}")
    public ResponseEntity<Pet> createPet(
            @PathVariable("shelter_id") ShelterId shelterId,
            @RequestBody Pet pet) {
        if (pet == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(petService.createPet(shelterId, pet));
    }

    @GetMapping("{shelter_id}/{pet_id}")
    public ResponseEntity<Pet> getPet(
            @PathVariable("shelter_id") ShelterId shelterId,
            @PathVariable("pet_id") Integer petId) {
        return ResponseEntity.ok(petService.getPet(shelterId, petId));
    }

    @PutMapping("{shelter_id}")
    public ResponseEntity<Pet> updatePet(
            @PathVariable("shelter_id") ShelterId shelterId,
            @RequestBody Pet pet) {
        if (pet == null) {
            return ResponseEntity.noContent().build();
        }
        Pet updatedPet = petService.updatePet(shelterId, pet);
        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("{shelter_id}/{pet_id}")
    public ResponseEntity<Pet> deletePet(
            @PathVariable("shelter_id") ShelterId shelterId,
            @PathVariable("pet_id") Integer petId) {
        Pet pet = petService.getPet(shelterId, petId);
        petService.deletePet(shelterId, petId);
        return ResponseEntity.ok(pet);
    }

    @GetMapping("{shelter_id}/all")
    public ResponseEntity<Collection<Pet>> getAllDogs(
            @PathVariable("shelter_id") ShelterId shelterId) {
        return ResponseEntity.ok(petService.getAllPets(shelterId));
    }
}

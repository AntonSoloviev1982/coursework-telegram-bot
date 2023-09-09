package pro.sky.courseworktelegrambot.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.courseworktelegrambot.entities.Dog;
import pro.sky.courseworktelegrambot.entities.DogAdoption;
import pro.sky.courseworktelegrambot.services.DogAdoptionService;

import java.util.Collection;

@RestController
@RequestMapping("dog/adoption")
public class DogAdoptionController {
    private final DogAdoptionService dogAdoptionService;

    public DogAdoptionController(DogAdoptionService dogAdoptionService) {
        this.dogAdoptionService = dogAdoptionService;
    }

    @PostMapping
    public ResponseEntity<DogAdoption> createDogAdoption(@RequestBody DogAdoption dogAdoption){
        if (dogAdoption == null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dogAdoptionService.createDogAdoption(dogAdoption));
    }
    @GetMapping("{dogId}")
    public ResponseEntity<DogAdoption> getDog(@PathVariable Integer dogId){
        DogAdoption dogAdoption = dogAdoptionService.getDogAdoption(dogId);
        if (dogAdoption == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dogAdoption);
    }
    @PutMapping
    public ResponseEntity<DogAdoption> updateDog(@RequestBody DogAdoption dogAdoption) {
        DogAdoption updateDogAdoption = dogAdoptionService.updateDogAdoption(dogAdoption);
        if (updateDogAdoption == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(updateDogAdoption);
    }
    @DeleteMapping("{dogId}")
    public ResponseEntity<DogAdoption> deleteDog(@PathVariable Integer dogId){
        dogAdoptionService.deleteDogAdoption(dogId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("all")
    public ResponseEntity<Collection> getAllDogs(){
        return ResponseEntity.ok(dogAdoptionService.getAllDogAdoption());
    }
}

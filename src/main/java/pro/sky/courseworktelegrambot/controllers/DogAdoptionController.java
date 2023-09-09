package pro.sky.courseworktelegrambot.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.courseworktelegrambot.entity.Dog;
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
    public ResponseEntity<Dog> createDog(@RequestBody Dog dog){
        if (dog == null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dogAdoptionService.createDogAdoption(dog));
    }
    @GetMapping("{dogId}")
    public ResponseEntity<Dog> getDog(@PathVariable Long dogId){
        Dog dog = dogAdoptionService.getDogAdoption(dogId);
        if (dog == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dog);
    }
    @PutMapping
    public ResponseEntity<Dog> updateDog(@RequestBody Dog dog) {
        Dog updateDog = dogAdoptionService.updateDogAdoption(dog);
        if (updateDog == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(updateDog);
    }
    @DeleteMapping("{dogId}")
    public ResponseEntity<Dog> deleteDog(@PathVariable Long dogId){
        dogAdoptionService.deleteDogAdoption(dogId);
        return ResponseEntity.ok().build();
    }
    @GetMapping("all")
    public ResponseEntity<Collection> getAllDogs(){
        return ResponseEntity.ok(dogAdoptionService.getAllDogAdoption());
    }
}

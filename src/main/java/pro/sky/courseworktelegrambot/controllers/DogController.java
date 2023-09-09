package pro.sky.courseworktelegrambot.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.sky.courseworktelegrambot.entity.Dog;
import pro.sky.courseworktelegrambot.services.DogService;

@RestController
@RequestMapping("dog")
public class DogController {
    private final DogService dogService;
    public DogController(DogService dogService){
        this.dogService = dogService;
    }
    @PostMapping
    public ResponseEntity<Dog> createDog(@RequestBody Dog dog){
        if (dog == null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dogService.createDog(dog));
    }
    @GetMapping("{dogId}")
    public ResponseEntity<Dog> getDog(@PathVariable Long dogId){
        Dog dog = dogService.getDog(dogId);
        if (dog == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dog);
    }
    @PutMapping
    public ResponseEntity<Dog> updateDog(@RequestBody Dog dog) {
        Dog updateDog = dogService.updateDog(dog);
        if (updateDog == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(updateDog);
    }
    @DeleteMapping("{dogId}")
    public ResponseEntity<Dog> deleteDog(@PathVariable Long dogId){
        dogService.deleteDog(dogId);
        return ResponseEntity.ok().build();
    }
}

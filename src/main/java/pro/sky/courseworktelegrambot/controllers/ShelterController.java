package pro.sky.courseworktelegrambot.controllers;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;
import pro.sky.courseworktelegrambot.entity.Shelter;
import pro.sky.courseworktelegrambot.services.ShelterService;

import java.util.List;

@RestController
@RequestMapping("/shelter")
public class ShelterController {

    private final ShelterService shelterService;

    public ShelterController(ShelterService shelterService) {
        this.shelterService = shelterService;
    }


    @PostMapping
    public Shelter create(@RequestBody Shelter shelter) {
        return shelterService.create(shelter);
    }

    @GetMapping("{id}")
    public Shelter get(@PathVariable("id") int id) {
        return shelterService.get(id);
    }

    @PutMapping("{id}")
    public Shelter update(@PathVariable("id") int id, Shelter shelter) {
        return shelterService.update(id, shelter);
    }

    @DeleteMapping("{id}")
    public Shelter delete(@PathVariable("id") int id) {
        return shelterService.delete(id);
    }

    @GetMapping
    public List<Shelter> findAll() {
        return shelterService.findAll();
    }




}

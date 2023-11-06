package pro.sky.courseworktelegrambot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
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

    @Operation(summary = "Создание нового питомца",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Созданный питомец",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый питомец",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class)
                    )
            )
    )
    @PostMapping("{shelter_id}")
    public ResponseEntity<Pet> createPet(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @RequestBody Pet pet) {
        if (pet == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(petService.createPet(shelterId, pet));
    }

    @Operation(summary = "Поиск питомца по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный питомец",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    )
            })
    @GetMapping("{shelter_id}/{pet_id}")
    public ResponseEntity<Pet> getPet(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор питомца")
            @PathVariable("pet_id") Integer petId) {
        return ResponseEntity.ok(petService.getPet(shelterId, petId));
    }

    @Operation(summary = "Изменение питомца",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Измененный питомец",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Измененный питомец",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class)
                    )
            )
    )
    @PutMapping("{shelter_id}")
    public ResponseEntity<Pet> updatePet(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @RequestBody Pet pet) {
        if (pet == null) {
            return ResponseEntity.noContent().build();
        }
        Pet updatedPet = petService.updatePet(shelterId, pet);
        return ResponseEntity.ok(updatedPet);
    }

    @Operation(summary = "Удаление питомца",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаленный питомец",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    )
            })
    @DeleteMapping("{shelter_id}/{pet_id}")
    public ResponseEntity<Pet> deletePet(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId,
            @Parameter(description = "Идентификатор питомца")
            @PathVariable("pet_id") Integer petId) {
        Pet pet = petService.getPet(shelterId, petId);
        petService.deletePet(shelterId, petId);
        return ResponseEntity.ok(pet);
    }

    @Operation(summary = "Поиск всех питомцев",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все найденные питомцы",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Pet.class)
                            )
                    )
            })
    @GetMapping("{shelter_id}/all")
    public ResponseEntity<Collection<Pet>> getAllDogs(
            @Parameter(description = "Идентификатор приюта")
            @PathVariable("shelter_id") ShelterId shelterId) {
        return ResponseEntity.ok(petService.getAllPets(shelterId));
    }
}

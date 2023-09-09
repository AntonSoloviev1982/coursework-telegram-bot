package pro.sky.courseworktelegrambot.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
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

    @Operation(summary = "Создание нового приюта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Созданный приют",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новый приют",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class)
                    )
            )
    )
    @PostMapping
    public Shelter create(@RequestBody Shelter shelter) {
        return shelterService.create(shelter);
    }

    @Operation(summary = "Поиск приюта по идентификатору",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный приют",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    )
            })
    @GetMapping("{id}")
    public Shelter get(@Parameter(description = "Идентификатор приюта")
                       @PathVariable("id") int id) {
        return shelterService.get(id);
    }

    @Operation(
            summary = "Изменение приюта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Измененный приют",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактируемый приют",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Shelter.class)
                    )
            )
    )
    @PutMapping("{id}")
    public Shelter update(@Parameter(description = "Идентификатор приюта")
                          @PathVariable("id") int id,
                          @RequestBody Shelter shelter) {
        return shelterService.update(id, shelter);
    }

    @Operation(
            summary = "Удаление приюта",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаленный приют",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Shelter.class)
                            )
                    )
            }
    )
    @DeleteMapping("{id}")
    public Shelter delete(@Parameter(description = "Идентификатор приюта")
                          @PathVariable("id") int id) {
        return shelterService.delete(id);
    }

    @Operation(
            summary = "Поиск всех приютов",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Все найденные приюты",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Shelter.class))
                            )
                    )
            }
    )
    @GetMapping
    public List<Shelter> findAll() {
        return shelterService.findAll();
    }


}

package pro.sky.courseworktelegrambot.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.courseworktelegrambot.entities.Pet;
import pro.sky.courseworktelegrambot.entities.ShelterId;
import pro.sky.courseworktelegrambot.services.PetService;

import java.util.Arrays;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * Тестовый класс для PetController
 */
@WebMvcTest(PetController.class)
public class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PetService petService;

    private Pet pet;

    @BeforeEach
    void setUp() {
        pet = new Pet();
        pet.setId(1);
        pet.setName("Тузик");
    }

    @Test
    void createPetTest() throws Exception {
        when(petService.createPet(any(ShelterId.class), any(Pet.class))).thenReturn(pet);

        mockMvc.perform(post("/pet/{shelter_id}", "DOG")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тузик"));
    }

    @Test
    void getPetTest() throws Exception {
        when(petService.getPet(any(ShelterId.class), any(Integer.class))).thenReturn(pet);

        mockMvc.perform(get("/pet/{shelter_id}/{pet_id}", "DOG", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тузик"));
    }

    @Test
    void updatePetTest() throws Exception {
        when(petService.updatePet(any(ShelterId.class), any(Pet.class))).thenReturn(pet);

        mockMvc.perform(put("/pet/{shelter_id}", "DOG")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pet)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тузик"));
    }

    @Test
    void deletePetTest() throws Exception {
        when(petService.getPet(any(ShelterId.class), any(Integer.class))).thenReturn(pet);
        when(petService.deletePet(any(ShelterId.class), any(Integer.class))).thenReturn(pet);

        mockMvc.perform(delete("/pet/{shelter_id}/{pet_id}", "DOG", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тузик"));
    }

    @Test
    void getAllPetsTest() throws Exception {
        Collection<Pet> pets = Arrays.asList(pet);
        when(petService.getAllPets(any(ShelterId.class))).thenReturn(pets);

        mockMvc.perform(get("/pet/{shelter_id}/all", "DOG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Тузик"));
    }
}
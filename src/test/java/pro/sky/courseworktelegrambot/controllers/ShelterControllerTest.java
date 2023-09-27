package pro.sky.courseworktelegrambot.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.courseworktelegrambot.entities.Shelter;
import pro.sky.courseworktelegrambot.repositories.ShelterRepository;
import pro.sky.courseworktelegrambot.services.ShelterService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ShelterController.class)
public class ShelterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShelterRepository shelterRepository;


    @SpyBean
    private ShelterService shelterService;

    @InjectMocks
    private ShelterController shelterController;

    @Autowired
    private ObjectMapper objectMapper;

    private Shelter shelter;

    @BeforeEach
    public void beforeEach() {
        shelter = new Shelter();
        shelter.setId("Dog");
        shelter.setName("Dogs");
        shelter.setInformation("information");
        shelter.setTimetable("timetable");
        shelter.setAddress("address");
        shelter.setSecurity("security");
        shelter.setSafetyPrecautions("safetyPrecautions");
        shelter.setRules("rules");
        shelter.setDocuments("documents");
        shelter.setTransportation("transportation");
        shelter.setChildAccomodation("childAccomodation");
        shelter.setAdultAccomodation("adultAccomodation");
        shelter.setInvalidAccomodation("invalidAccomodation");
        shelter.setCommunication("communication");
        shelter.setCynologists("cynologists");
        shelter.setRefusalReasons("refusalReasons");
    }

    @Test
    public void createTest() throws Exception {
        when(shelterRepository.save(any())).thenReturn(shelter);

        mockMvc.perform(
                        post("/shelter")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(shelter))
                ).andExpect(status().isOk())
                .andExpect(result -> {
                    Shelter shelter1 = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            Shelter.class
                    );
                    assertThat(shelter1).isNotNull();
                    assertThat(shelter1.getId()).isEqualTo(shelter.getId());
                    assertThat(shelter1.getName()).isEqualTo(shelter.getName());
                    assertThat(shelter1.getInformation()).isEqualTo(shelter.getInformation());
                    assertThat(shelter1.getTimetable()).isEqualTo(shelter.getTimetable());
                    assertThat(shelter1.getAddress()).isEqualTo(shelter.getAddress());
                    assertThat(shelter1.getSecurity()).isEqualTo(shelter.getSecurity());
                    assertThat(shelter1.getSafetyPrecautions()).isEqualTo(shelter.getSafetyPrecautions());
                    assertThat(shelter1.getRules()).isEqualTo(shelter.getRules());
                    assertThat(shelter1.getDocuments()).isEqualTo(shelter.getDocuments());
                    assertThat(shelter1.getTransportation()).isEqualTo(shelter.getTransportation());
                    assertThat(shelter1.getChildAccomodation()).isEqualTo(shelter.getChildAccomodation());
                    assertThat(shelter1.getAdultAccomodation()).isEqualTo(shelter.getAdultAccomodation());
                    assertThat(shelter1.getInvalidAccomodation()).isEqualTo(shelter.getInvalidAccomodation());
                    assertThat(shelter1.getCommunication()).isEqualTo(shelter.getCommunication());
                    assertThat(shelter1.getCynologists()).isEqualTo(shelter.getCynologists());
                    assertThat(shelter1.getRefusalReasons()).isEqualTo(shelter.getRefusalReasons());
                });
        verify(shelterRepository, new Times(1)).save(any());
    }

    @Test
    public void getTest() throws Exception {
        when(shelterRepository.findById(eq("Dog"))).thenReturn(Optional.of(shelter));

        mockMvc.perform(
                        get("/shelter/Dog")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(result -> {
                    Shelter shelter1 = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            Shelter.class
                    );
                    assertThat(shelter1).isNotNull();
                    assertThat(shelter1.getId()).isEqualTo(shelter.getId());
                    assertThat(shelter1.getName()).isEqualTo(shelter.getName());
                    assertThat(shelter1.getInformation()).isEqualTo(shelter.getInformation());
                    assertThat(shelter1.getTimetable()).isEqualTo(shelter.getTimetable());
                    assertThat(shelter1.getAddress()).isEqualTo(shelter.getAddress());
                    assertThat(shelter1.getSecurity()).isEqualTo(shelter.getSecurity());
                    assertThat(shelter1.getSafetyPrecautions()).isEqualTo(shelter.getSafetyPrecautions());
                    assertThat(shelter1.getRules()).isEqualTo(shelter.getRules());
                    assertThat(shelter1.getDocuments()).isEqualTo(shelter.getDocuments());
                    assertThat(shelter1.getTransportation()).isEqualTo(shelter.getTransportation());
                    assertThat(shelter1.getChildAccomodation()).isEqualTo(shelter.getChildAccomodation());
                    assertThat(shelter1.getAdultAccomodation()).isEqualTo(shelter.getAdultAccomodation());
                    assertThat(shelter1.getInvalidAccomodation()).isEqualTo(shelter.getInvalidAccomodation());
                    assertThat(shelter1.getCommunication()).isEqualTo(shelter.getCommunication());
                    assertThat(shelter1.getCynologists()).isEqualTo(shelter.getCynologists());
                    assertThat(shelter1.getRefusalReasons()).isEqualTo(shelter.getRefusalReasons());
                });
        verify(shelterRepository, new Times(1)).findById(eq("Dog"));

        //not found checking

        when(shelterRepository.findById(eq("Cat"))).thenReturn(Optional.empty());
        mockMvc.perform(
                        get("/shelter/Cat")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isEqualTo("Shelter with id: Cat is not found!");
                });

    }

//    @Test
//    public void updateTest() throws Exception {
//        String informationType = "address";
//        String newInformation = "new address";
//        Shelter updateShelter = shelter;
//        updateShelter.setAddress(newInformation);
//        when(shelterRepository.save(any())).thenReturn(updateShelter);
//        mockMvc.perform(
//                        put("/shelter/Dog/?informationType={address}&newInformation={new address}"
//                                , informationType, newInformation)
//                                .contentType(MediaType.APPLICATION_JSON)
//                ).andExpect(status().isOk())
//                .andExpect(result -> {
//                    Shelter shelter1 = objectMapper.readValue(
//                            result.getResponse().getContentAsString(),
//                            Shelter.class
//                    );
//                    assertThat(shelter1.getAddress()).isEqualTo(updateShelter.getAddress());
//                });
//    }

    @Test
    public void deleteTest() throws Exception {
        when(shelterRepository.findById(eq("Dog"))).thenReturn(Optional.of(shelter));

        mockMvc.perform(
                delete("/shelter/Dog")
        ).andExpect(status().isOk());
        verify(shelterRepository, new Times(1)).deleteById(eq("Dog"));
        Mockito.reset(shelterRepository);

        //not found checking

        when(shelterRepository.findById(eq("Cat"))).thenReturn(Optional.empty());

        mockMvc.perform(
                        delete("/shelter/Cat")
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseString = result.getResponse().getContentAsString();
                    assertThat(responseString).isEqualTo("Shelter with id: Cat is not found!");
                });
    }


    @Test
    public void findAllTest() throws Exception {
        List<Shelter> shelters = new ArrayList<>();
        shelters.add(shelter);

        when(shelterRepository.findAll()).thenReturn(shelters);

        mockMvc.perform(
                        get("/shelter")
                ).andExpect(status().isOk())
                .andExpect(result -> {
                    List<Shelter> shelters1 = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<List<Shelter>>() {
                            }
                    );
                    assertThat(shelters1).isNotNull().isNotEmpty();
                    assertThat(shelters.size()).isEqualTo(shelters1.size());
                });
    }

}

package pro.sky.courseworktelegrambot.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.mockito.junit.jupiter.MockitoExtension;
import pro.sky.courseworktelegrambot.entities.Shelter;
import pro.sky.courseworktelegrambot.exceptions.ShelterNotFoundException;
import pro.sky.courseworktelegrambot.repositories.ShelterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelterServiceTest {

    @Mock
    private ShelterRepository shelterRepository;

    @InjectMocks
    private ShelterService shelterService;

    private Shelter shelter1;

    @BeforeEach
    public void beforeEach() {
        shelter1 = new Shelter();
        shelter1.setId("Dog");
        shelter1.setName("Dogs");
        shelter1.setInformation("information");
        shelter1.setTimetable("timetable");
        shelter1.setAddress("address");
        shelter1.setSecurity("security");
        shelter1.setSafetyPrecautions("safetyPrecautions");
        shelter1.setRules("rules");
        shelter1.setDocuments("documents");
        shelter1.setTransportation("transportation");
        shelter1.setChildAccomodation("childAccomodation");
        shelter1.setAdultAccomodation("adultAccomodation");
        shelter1.setInvalidAccomodation("invalidAccomodation");
        shelter1.setCommunication("communication");
        shelter1.setCynologists("cynologists");
        shelter1.setRefusalReasons("refusalReasons");
    }

    @Test
    public void createTest() {
        when(shelterRepository.save(shelter1)).thenReturn(shelter1);
        assertThat(shelterService.create(shelter1)).isEqualTo(shelter1);
        verify(shelterRepository, atLeast(1)).save(shelter1);
    }

    @Test
    public void getTest() {
        when(shelterRepository.findById("Dog")).thenReturn(Optional.of(shelter1));
        assertThat(shelterService.get("Dog")).isEqualTo(shelter1);
        verify(shelterRepository, atLeast(1)).findById("Dog");
    }

    @Test
    public void getNegativeTest() {
        when(shelterRepository.findById("Cat")).thenReturn(Optional.empty());
        assertThatExceptionOfType(ShelterNotFoundException.class).isThrownBy(() -> shelterService.get("Cat"));
        verify(shelterRepository, atLeast(1)).findById("Cat");
    }


    @Test
    public void deleteTest() {
        when(shelterRepository.findById("Dog")).thenReturn(Optional.of(shelter1));
        Mockito.doNothing().when(shelterRepository).deleteById("Dog");
        shelterService.delete("Dog");
        verify(shelterRepository, atLeast(1)).deleteById("Dog");
    }

    @Test
    public void deleteNegativeTest() {
        when(shelterRepository.findById("Cat")).thenReturn(Optional.empty());
        assertThatExceptionOfType(ShelterNotFoundException.class).isThrownBy(() -> shelterService.delete("Cat"));
        verify(shelterRepository, new Times(0)).deleteById("Dog");
    }

    @Test
    public void findAllTest() {
        List<Shelter> shelterList = new ArrayList<>();
        shelterList.add(shelter1);
        when(shelterRepository.findAll()).thenReturn(shelterList);
        assertThat(shelterService.findAll())
                .hasSize(1)
                .containsExactlyInAnyOrder(shelter1);
    }

}

package pro.sky.courseworktelegrambot.controllers;

import org.springframework.web.bind.annotation.*;
import pro.sky.courseworktelegrambot.entity.MessageToVolunteer;
import pro.sky.courseworktelegrambot.services.MessageToVolunteerService;

import java.util.List;

@RestController
@RequestMapping("/message_to_volunteer")
public class MessageToVolunteerController {

    private final MessageToVolunteerService messageToVolunteerService;

    public MessageToVolunteerController(MessageToVolunteerService messageToVolunteerService) {
        this.messageToVolunteerService = messageToVolunteerService;
    }

    @GetMapping("/all_without_answer")
    public List<MessageToVolunteer> findAllWithoutAnswer() {
        return messageToVolunteerService.findAllWithoutAnswer();
    }

    @PutMapping("{id}/answer")
    public void updateAnswer(@PathVariable("id") int id, @RequestParam String answer) {
        messageToVolunteerService.updateAnswer(id, answer);
    }

}

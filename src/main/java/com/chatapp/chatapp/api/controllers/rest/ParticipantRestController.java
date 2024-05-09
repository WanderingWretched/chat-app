package com.chatapp.chatapp.api.controllers.rest;

import com.chatapp.chatapp.api.dto.ChatDTO;
import com.chatapp.chatapp.api.dto.ParticipantDTO;
import com.chatapp.chatapp.factories.ParticipantDtoFactory;
import com.chatapp.chatapp.services.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ParticipantRestController {

    ParticipantService participantService;

    ParticipantDtoFactory participantDtoFactory;

    @Autowired
    ParticipantRestController(ParticipantService participantService, ParticipantDtoFactory participantDtoFactory) {
        this.participantService = participantService;
        this.participantDtoFactory = participantDtoFactory;
    }


    public static final String FETCH_PARTICIPANTS = "/api/chats/{chat_id}/participants";

    @GetMapping(value = FETCH_PARTICIPANTS,produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ParticipantDTO> fetchParticipants(@PathVariable("chat_id") String chatId) {
        return participantService
                .getParticipant(chatId)
                .map(participantDtoFactory::makeParticipantDTO)
                .collect(Collectors.toList());
    }
}

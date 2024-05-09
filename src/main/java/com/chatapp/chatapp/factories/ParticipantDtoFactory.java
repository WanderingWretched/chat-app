package com.chatapp.chatapp.factories;


import com.chatapp.chatapp.api.domains.Participant;
import com.chatapp.chatapp.api.dto.ParticipantDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ParticipantDtoFactory {
    public ParticipantDTO makeParticipantDTO(Participant participant) {
        ParticipantDTO participantDTO = new ParticipantDTO();
        participantDTO.setId(participant.getId());
        participantDTO.setEnteredAt(Instant.ofEpochMilli(participant.getEnteredAt()));
        return participantDTO;

    }
}

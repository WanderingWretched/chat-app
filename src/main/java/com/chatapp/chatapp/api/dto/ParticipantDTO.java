package com.chatapp.chatapp.api.dto;

import java.time.Instant;

public class ParticipantDTO {
    private String id;

    Instant enteredAt = Instant.now();


    public Instant getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(Instant enteredAt) {
        this.enteredAt = enteredAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

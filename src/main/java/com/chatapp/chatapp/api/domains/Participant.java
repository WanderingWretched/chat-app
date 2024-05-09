package com.chatapp.chatapp.api.domains;

import jakarta.persistence.Entity;

import java.io.Serializable;
import java.time.Instant;

@Entity
public class Participant implements Serializable {

    Long enteredAt = Instant.now().toEpochMilli();

    private String sessionId;
    private String id;
    private String chatId;


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(Long enteredAt) {
        this.enteredAt = enteredAt;
    }
}

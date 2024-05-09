package com.chatapp.chatapp.api.dto;

import java.time.Instant;

public class MessageDTO {
    private String from;
    private String message;

    Instant cratedAt = Instant.now();

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCratedAt() {
        return cratedAt;
    }

    public void setCratedAt(Instant cratedAt) {
        this.cratedAt = cratedAt;
    }
}

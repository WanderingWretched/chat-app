package com.chatapp.chatapp.api.domains;

import com.chatapp.chatapp.api.RandomIdGenerator;
import jakarta.persistence.Entity;

import java.io.Serializable;
import java.time.Instant;

@Entity
public class Chat implements Serializable {

    private String id = RandomIdGenerator.generate();

    private String name;

    Long createdAt = Instant.now().toEpochMilli();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}

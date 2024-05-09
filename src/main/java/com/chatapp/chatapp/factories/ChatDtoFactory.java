package com.chatapp.chatapp.factories;

import com.chatapp.chatapp.api.domains.Chat;
import com.chatapp.chatapp.api.dto.ChatDTO;

import java.time.Instant;

public class ChatDtoFactory {
    public ChatDTO makeChatDto(Chat chat) {
        ChatDTO chatDto = new ChatDTO();
        chatDto.setId(chat.getId());
        chatDto.setName(chat.getName());
        chatDto.setCreatedAt(Instant.ofEpochMilli(chat.getCreatedAt()));
        return chatDto;
    }
}

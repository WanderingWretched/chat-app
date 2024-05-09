package com.chatapp.chatapp.api.controllers.rest;


import com.chatapp.chatapp.api.dto.ChatDTO;
import com.chatapp.chatapp.factories.ChatDtoFactory;
import com.chatapp.chatapp.services.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ChatRestController {
    ChatService chatService;

    ChatDtoFactory chatDtoFactory;

    @Autowired
    ChatRestController(
            ChatService chatService,
            ChatDtoFactory chatDtoFactory
    ) {
        this.chatService = chatService;
        this.chatDtoFactory = chatDtoFactory;

    }

    public static final String FETCH_CHATS = "/api/chats";

    @GetMapping(value = FETCH_CHATS,produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ChatDTO> fetchChats() {
        return chatService
                .getChats()
                .map(chatDtoFactory::makeChatDto)
                .collect(Collectors.toList());
    }
}

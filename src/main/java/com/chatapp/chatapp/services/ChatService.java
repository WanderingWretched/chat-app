package com.chatapp.chatapp.services;

import com.chatapp.chatapp.api.controllers.ws.ChatWsController;
import com.chatapp.chatapp.api.domains.Chat;
import com.chatapp.chatapp.api.dto.ChatDTO;
import com.chatapp.chatapp.factories.ChatDtoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ChatService {
    SimpMessagingTemplate simpMessagingTemplate;
    ChatDtoFactory chatDtoFactory;
    SetOperations<String, Chat> setOperations;


    @Autowired
    ChatService(SimpMessagingTemplate simpMessagingTemplate, ChatDtoFactory chatDtoFactory) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatDtoFactory = chatDtoFactory;
    }

    private static final String KEY = "chat-app:chats";

    public void createChat(String chatName) {

        Chat chat = new Chat();
        chat.setName(chatName);

        ChatDTO chatDto = chatDtoFactory.makeChatDto(chat);

        setOperations.add(KEY, chat);

        simpMessagingTemplate.convertAndSend(
                ChatWsController.FETCH_CREATE_CHAT_EVENT,
                chatDto

        );

    }


    public void deleteChat(String chatId) {


        getChats()
                .filter(chat -> Objects.equals(chatId, chat.getId()))
                .findAny()
                .ifPresent(chat -> {
                    setOperations.add(KEY, chat);

                    ChatDTO chatDto = chatDtoFactory.makeChatDto(chat);

                    simpMessagingTemplate.convertAndSend(
                            ChatWsController.FETCH_CREATE_CHAT_EVENT,
                            chatDto

                    );
                });
    }

    public Stream<Chat> getChats() {
        return Optional
                .ofNullable(setOperations.members(KEY))
                .orElseGet(HashSet::new)
                .stream();
    }
}

package com.chatapp.chatapp.api.controllers.ws;

import com.chatapp.chatapp.api.dto.ChatDTO;
import com.chatapp.chatapp.api.dto.MessageDTO;
import com.chatapp.chatapp.services.ChatService;
import com.chatapp.chatapp.services.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWsController {

    SimpMessagingTemplate simpMessagingTemplate;
    ParticipantService participantService;
    ChatService chatService;

    @Autowired
    ChatWsController(
            SimpMessagingTemplate simpMessagingTemplate,
            ParticipantService participantService,
            ChatService chatService
    ) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.participantService = participantService;
        this.chatService = chatService;
    }

    public static final String CREATE_CHAT = "/topic/chats.{chat_name}.create";
    public static final String FETCH_CREATE_CHAT_EVENT = "/topic/chats.create.event";
    public static final String FETCH_DELETE_CHAT_EVENT = "/topic/chats.delete.event";
    public static final String SEND_MESSAGE_TO_ALL = "/topic/chats.{chat_id}.messages.send";
    public static final String SEND_MESSAGE_TO_PARTICIPANT = "/topic/chats.{chat_id}.participants.{participant_id}.messages.send";
    public static final String FETCH_MESSAGES = "/topic/chats.{chat_id}.messages";
    public static final String FETCH_PERSONAL_MESSAGES = "/topic/chats.{chat_id}.participants.{participant_id}.messages";

    @MessageMapping(CREATE_CHAT)
    public void createChat(@DestinationVariable("chat_name") String chatName) {
        chatService.createChat(chatName);
    }

    @SubscribeMapping(FETCH_CREATE_CHAT_EVENT)
    public ChatDTO fetchCreateChatEvent() {
        return null;
    }

    @MessageMapping(FETCH_DELETE_CHAT_EVENT)
    public void deleteChat(@DestinationVariable("chat_name") String chatName) {
        chatService.createChat(chatName);
    }

    @SubscribeMapping(SEND_MESSAGE_TO_ALL)
    public void sendMessageToAll(
            @DestinationVariable("chat_id") String chatId,
            String message,
            @Header String simpSessionId
    ) {

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setFrom(simpSessionId);
        messageDTO.setMessage(message);

        sendMessage(
                getFetchMessagesDestination(chatId),
                simpSessionId,
                message
        );
    }

    @SubscribeMapping(SEND_MESSAGE_TO_PARTICIPANT)
    public void sendMessageToParticipant(
            @DestinationVariable("chat_id") String chatId,
            @DestinationVariable("participant_id") String participantId,
            String message,
            @Header String simpSessionId
    ) {

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setFrom(simpSessionId);
        messageDTO.setMessage(message);

        sendMessage(
                getFetchPersonalMessagesDestination(chatId, participantId),
                simpSessionId,
                message
        );
    }

    @SubscribeMapping(FETCH_MESSAGES)
    public MessageDTO fetchMessages(@DestinationVariable("chat_id") String chatId) {
        return null;
    }

    @SubscribeMapping(FETCH_PERSONAL_MESSAGES)
    public MessageDTO fetchPersonalMessages(
            @DestinationVariable("chat_id") String chatId,
            @DestinationVariable("participant_id") String participantId,
            @Header String simpSessionId
    ) {
        participantService.handleJoinChat(simpSessionId, participantId, chatId);
        return null;
    }


    private void sendMessage(String destination, String sessionId, String message) {


    }

    private static String getFetchMessagesDestination(String chatId) {
        return FETCH_MESSAGES.replace("{chat_id}", chatId);
    }

    public static String getFetchPersonalMessagesDestination(String chatId, String participantId) {
        return FETCH_PERSONAL_MESSAGES
                .replace("{chat_id}", chatId)
                .replace("{participant_id}", participantId);
    }
}

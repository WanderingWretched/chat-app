package com.chatapp.chatapp.services;


import com.chatapp.chatapp.api.domains.Participant;
import com.chatapp.chatapp.api.dto.ParticipantDTO;
import com.chatapp.chatapp.api.controllers.ws.ParticipantWsController;
import com.chatapp.chatapp.factories.ParticipantDtoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
public class ParticipantService {
    SimpMessagingTemplate simpMessagingTemplate;
    ParticipantDtoFactory participantDtoFactory;

    ChatService chatService;
    SetOperations<String, Participant> setOperations;

    private static final Map<String, Participant> sessionIdToParticipantMap = new ConcurrentHashMap<>();

    @Autowired
    ParticipantService(
            SimpMessagingTemplate simpMessagingTemplate,
            ParticipantDtoFactory participantDtoFactory,
            ChatService chatService
    ) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.participantDtoFactory = participantDtoFactory;
        this.chatService = chatService;
    }

    public Stream<Participant> getParticipant(String chatId) {
        return Optional
                .ofNullable(setOperations.members(ParticipantKeyHelper.makeKey(chatId)))
                .orElseGet(HashSet::new)
                .stream();
    }

    public void handleJoinChat(String sessionId, String participantId, String chatId) {

        Participant participant = new Participant();
        participant.setSessionId(sessionId);
        participant.setId(participantId);
        participant.setChatId(chatId);

        sessionIdToParticipantMap.put(participant.getSessionId(), participant);

        setOperations.add(ParticipantKeyHelper.makeKey(chatId), participant);

        simpMessagingTemplate.convertAndSend(
                ParticipantWsController.getFetchParticipantJoinInChatDestination(chatId),
                participantDtoFactory.makeParticipantDTO(participant)
        );
    }

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event){

    }
    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        handleLeaveChat(event);
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        handleLeaveChat(event);
    }



    private void handleLeaveChat(AbstractSubProtocolEvent event) {

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

        Optional
                .ofNullable(headerAccessor.getSessionId())
                .map(sessionIdToParticipantMap::remove)
                .ifPresent(participant -> {

                    String chatId = participant.getChatId();

                    String key = ParticipantKeyHelper.makeKey(chatId);
                    setOperations.remove(
                            key,
                            participant
                    );

                    ParticipantDTO participantDTO = participantDtoFactory.makeParticipantDTO(participant);

                    Optional
                            .ofNullable(setOperations.size(key))
                            .filter(size -> size == 0L)
                            .ifPresent(size -> {
                                chatService.deleteChat(chatId);
                            });

                    simpMessagingTemplate.convertAndSend(
                            key,
                            participantDTO
                    );
                });
    }


    private static class ParticipantKeyHelper {

        private static final String KEY = "chat-app:chats:{chat_id}:participants";

        public static String makeKey(String chatId) {
            return KEY.replace("{chat_id}", chatId);
        }
    }

}


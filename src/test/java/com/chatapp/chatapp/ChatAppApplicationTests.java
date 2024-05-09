package com.chatapp.chatapp;

import com.chatapp.chatapp.api.RandomIdGenerator;
import com.chatapp.chatapp.api.controllers.rest.ChatRestController;
import com.chatapp.chatapp.api.controllers.ws.ChatWsController;
import com.chatapp.chatapp.config.WebSocketConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChatAppApplicationTests {
    @Value("${local.server.port}")
    private int port;

    private static WebClient client;

    final ObjectMapper mapper;
    final MockMvc mockMvc;


    @Autowired
    ChatAppApplicationTests(ObjectMapper mapper, MockMvc mockMvc) {
        this.mapper = mapper;
        this.mockMvc = mockMvc;

    }

    @BeforeAll
    public void setup() throws Exception {

        RunStopFrameHandler runStopFrameHandler = new RunStopFrameHandler(new CompletableFuture<>());

        String wsUrl = "ws://127.0.0.1:" + port + WebSocketConfig.REGISTRY;

        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));

        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient
                .connect(wsUrl, new StompSessionHandlerAdapter() {
                })
                .get(1, TimeUnit.SECONDS);

        WebClient webClient = new WebClient();
        webClient.setStompClient(stompClient);
        webClient.setStompSession(stompSession);
        webClient.setHandler(runStopFrameHandler);

        client = webClient;
    }

    @AfterAll
    public void tearDown() {

        if (client.getStompSession().isConnected()) {
            client.getStompSession().disconnect();
            client.getStompClient().stop();
        }
    }

    @Test
    public void should_PassSuccessfully_When_CreateChat() throws Exception {

        StompSession stompSession = client.getStompSession();

        RunStopFrameHandler handler = client.getHandler();

        String chatName = "Crazy chat";

        stompSession.send(
                ChatWsController.CREATE_CHAT,
                chatName
        );

        String contentAsString = mockMvc
                .perform(MockMvcRequestBuilders.get(ChatRestController.FETCH_CHATS))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<LinkedHashMap<String, Object>> params =
                (List<LinkedHashMap<String, Object>>) mapper.readValue(contentAsString, List.class);

        Assertions.assertFalse(params.isEmpty());

        String chatId = (String) params.get(0).get("id");

        String destination = ChatWsController.getFetchPersonalMessagesDestination(chatId, RandomIdGenerator.generate());

        final RunStopFrameHandler runStopFrameHandler = new RunStopFrameHandler(new CompletableFuture<>());
        stompSession.subscribe(
                destination,
                runStopFrameHandler
        );
    }

    private List<Transport> createTransportClient() {

        List<Transport> transports = new ArrayList<>(1);

        transports.add(new WebSocketTransport(new StandardWebSocketClient()));

        return transports;
    }

    private class RunStopFrameHandler implements StompFrameHandler {
        public RunStopFrameHandler(CompletableFuture<Object> objectCompletableFuture) {
        }


        CompletableFuture<Object> future;


        @Override
        public @NonNull Type getPayloadType(StompHeaders stompHeaders) {

            System.out.println(stompHeaders.toString());

            return byte[].class;
        }

        @Override
        public void handleFrame(@NonNull StompHeaders stompHeaders, Object o) {
            System.out.println(o);

            future.complete(o);

            future = new CompletableFuture<>();
        }
    }

    private static class WebClient {

        WebSocketStompClient stompClient;

        StompSession stompSession;

        String sessionToken;

        RunStopFrameHandler handler;

        public WebSocketStompClient getStompClient() {
            return stompClient;
        }

        public void setStompClient(WebSocketStompClient stompClient) {
            this.stompClient = stompClient;
        }

        public StompSession getStompSession() {
            return stompSession;
        }

        public void setStompSession(StompSession stompSession) {
            this.stompSession = stompSession;
        }

        public String getSessionToken() {
            return sessionToken;
        }

        public void setSessionToken(String sessionToken) {
            this.sessionToken = sessionToken;
        }

        public RunStopFrameHandler getHandler() {
            return handler;
        }

        public void setHandler(RunStopFrameHandler handler) {
            this.handler = handler;
        }
    }
}

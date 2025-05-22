package com.instantrip.instantrip_backend.domain.trip_plan.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Slf4j
@Component
@RequiredArgsConstructor
public class TripWebSocketHandler extends TextWebSocketHandler {

    private final TripWebSocketService tripWebSocketService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        tripWebSocketService.registerSession(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        tripWebSocketService.handleMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.warn("Transport error: {}", exception.getMessage());
        tripWebSocketService.removeSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        tripWebSocketService.removeSession(session);
    }

}

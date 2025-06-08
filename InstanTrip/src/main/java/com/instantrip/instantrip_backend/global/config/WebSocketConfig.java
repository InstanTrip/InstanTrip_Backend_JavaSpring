package com.instantrip.instantrip_backend.global.config;

import com.instantrip.instantrip_backend.domain.trip_plan.TripPlanService;
import com.instantrip.instantrip_backend.domain.trip_plan.websocket.TripHandshakeInterceptor;
import com.instantrip.instantrip_backend.domain.trip_plan.websocket.TripWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
@EnableScheduling
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final TripPlanService tripPlanService;
    private final TripWebSocketHandler tripWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(tripWebSocketHandler, "/ws/trip")
                .addInterceptors(new TripHandshakeInterceptor(tripPlanService)) // 인터셉터 추가
                .setAllowedOrigins("*"); // CORS 설정
    }

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}

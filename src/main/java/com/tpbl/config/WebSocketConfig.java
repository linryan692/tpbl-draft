// src/main/java/com/tpbl/config/WebSocketConfig.java
package com.tpbl.config;

import com.tpbl.websocket.DraftWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DraftWebSocketHandler draftWebSocketHandler;

    public WebSocketConfig(DraftWebSocketHandler draftWebSocketHandler) {
        this.draftWebSocketHandler = draftWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
          .addHandler(draftWebSocketHandler, "/ws/draft")
          .setAllowedOrigins("*");
    }
}

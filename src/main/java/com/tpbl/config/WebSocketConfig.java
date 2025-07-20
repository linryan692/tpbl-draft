// src/main/java/com/tpbl/config/WebSocketConfig.java
package com.tpbl.config;

import com.tpbl.websocket.DraftWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private DraftWebSocketHandler draftWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
          .addHandler(draftWebSocketHandler, "/ws/draft")
          .setAllowedOrigins("*")
          // 把 Spring Security Context 放到 session attributes 裡面
          .addInterceptors(new org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor());
    }
}

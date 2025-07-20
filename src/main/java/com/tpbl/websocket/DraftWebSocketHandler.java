// src/main/java/com/tpbl/websocket/DraftWebSocketHandler.java
package com.tpbl.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tpbl.model.Pick;
import com.tpbl.model.Team;
import com.tpbl.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DraftWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private DraftService draftService;

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        sendFullStatus(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 解析前端請求
        PickRequest req = mapper.readValue(message.getPayload(), PickRequest.class);

        // 從 session attribute 拿出登入的 Team (透過 Spring Security HandshakeInterceptor)
        Team me = (Team) session.getAttributes()
            .get("SPRING_SECURITY_CONTEXT"); // 如用 interceptor 放入的 key

        // 驗證：只有輪到的隊伍可下指令
        Team current = draftService.currentTeam();
        if (me == null || current == null || !current.getId().equals(me.getId())) {
            return;
        }

        // 執行指名或放棄
        Pick pick = draftService.makePick(me.getId(), req.playerId);

        broadcast(new DraftEvent("NEW_PICK", pick));
        if (draftService.isDraftEnded()) {
            broadcast(new DraftEvent("DRAFT_ENDED", null));
        } else {
            broadcast(new DraftEvent("NEXT_TURN", draftService.currentTeam()));
        }
        broadcastFullStatusToAll();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    // ----- helper methods -----

    private void sendFullStatus(WebSocketSession session) throws Exception {
        Status s = new Status();
        s.teams = draftService.getTeamOrder();
        s.currentTeam = draftService.currentTeam();
        s.availablePlayers = draftService.availablePlayers();
        s.picks = draftService.allPicks();
        DraftEvent full = new DraftEvent("FULL_STATUS", s);
        session.sendMessage(new TextMessage(mapper.writeValueAsString(full)));
    }

    private void broadcast(DraftEvent evt) throws Exception {
        String pay = mapper.writeValueAsString(evt);
        for (WebSocketSession s : sessions) {
            s.sendMessage(new TextMessage(pay));
        }
    }

    private void broadcastFullStatusToAll() throws Exception {
        Status s = new Status();
        s.teams = draftService.getTeamOrder();
        s.currentTeam = draftService.currentTeam();
        s.availablePlayers = draftService.availablePlayers();
        s.picks = draftService.allPicks();
        broadcast(new DraftEvent("FULL_STATUS", s));
    }

    // ---- DTOs ----

    private static class DraftEvent {
        public String type;
        public Object data;
        public DraftEvent(String type, Object data) {
            this.type = type;
            this.data = data;
        }
    }

    private static class Status {
        public List<Team> teams;
        public Team currentTeam;
        public List<?> availablePlayers;
        public List<Pick> picks;
    }

    public static class PickRequest {
        public Long playerId;
    }
}

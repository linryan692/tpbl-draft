// src/main/java/com/tpbl/websocket/DraftWebSocketHandler.java
package com.tpbl.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tpbl.config.TeamUserDetails;
import com.tpbl.model.Pick;
import com.tpbl.model.Team;
import com.tpbl.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;           // ← 加回來
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component  // ← 這行缺了就不會被 Spring 掃描
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
        PickRequest req = mapper.readValue(message.getPayload(), PickRequest.class);

        SecurityContext sc = (SecurityContext) session.getAttributes().get("SPRING_SECURITY_CONTEXT");
        TeamUserDetails ud = (TeamUserDetails) sc.getAuthentication().getPrincipal();
        Team me = ud.getTeam();

        Team current = draftService.currentTeam();
        if (me == null || current == null || !current.getId().equals(me.getId())) {
            return;
        }

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

    private static class DraftEvent {
        public String type;
        public Object data;
        public DraftEvent(String type, Object data) {
            this.type = type; this.data = data;
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
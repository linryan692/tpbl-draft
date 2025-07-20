package com.tpbl.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tpbl.model.Pick;
import com.tpbl.model.Player;
import com.tpbl.model.Team;
import com.tpbl.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DraftWebSocketHandler extends TextWebSocketHandler {
  @Autowired private DraftService draftService;
  private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
  private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    sessions.add(session);
    broadcastStatus(session);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage msg) throws Exception {
    PickRequest req = mapper.readValue(msg.getPayload(), PickRequest.class);

    // **1. 从登录的 Principal 拿用户名，映射到 teamId**
    String username = session.getPrincipal().getName();
    Team me = draftService.findTeamByUsername(username);
    Long myTeamId = me.getId();

    // **2. 验证「只能在轮到自己时才能 pick/pass」**
    Team current = draftService.currentTeam();
    if (current==null || !current.getId().equals(myTeamId)) {
      // 非法操作：不是自己轮次
      session.sendMessage(new TextMessage(
        mapper.writeValueAsString(new DraftEvent("ERROR","現在不是您球團的回合"))
      ));
      return;
    }

    // **3. 调用 makePick，忽略前端 req.teamId**
    Pick pick = draftService.makePick(myTeamId, req.playerId);

    // 推送最新状态给所有人
    broadcastFullStatusToAll();
  }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    // 单条事件广播
    private void broadcast(DraftEvent event) throws Exception {
        String payload = mapper.writeValueAsString(event);
        for (WebSocketSession sess : sessions) {
            sess.sendMessage(new TextMessage(payload));
        }
    }

    // 首次连接时只给单个 session 全量状态
    private void broadcastStatus(WebSocketSession session) throws Exception {
        Status status = new Status();
        // 👇 从 draftService 拿永远不变的 allTeams
        status.teams            = draftService.getAllTeams();
        status.currentTeam      = draftService.currentTeam();
        status.availablePlayers = draftService.availablePlayers();
        status.picks            = draftService.allPicks();

        session.sendMessage(new TextMessage(mapper.writeValueAsString(new DraftEvent("FULL_STATUS", status))));
    }

    // 给所有客户端推最新 FULL_STATUS
    private void broadcastFullStatusToAll() throws Exception {
        Status status = new Status();
        status.teams            = draftService.getTeamOrder();       // ← 一定要加上这一行
        status.currentTeam      = draftService.currentTeam();
        status.availablePlayers = draftService.availablePlayers();
        status.picks            = draftService.allPicks();
        broadcast(new DraftEvent("FULL_STATUS", status));
    }

    // DTOs
    private static class DraftEvent {
        public String type;
        public Object data;
        public DraftEvent(String type, Object data) {
            this.type = type;
            this.data = data;
        }
    }

    public static class PickRequest {
        public Long teamId;
        public Long playerId;
    }

    private static class Status {
        public List<Team>  teams;
        public Team        currentTeam;
        public List<Player> availablePlayers;
        public List<Pick>   picks;
    }
}

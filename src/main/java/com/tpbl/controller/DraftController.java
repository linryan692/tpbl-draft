package com.tpbl.controller;

import com.tpbl.model.Pick;
import com.tpbl.model.Player;
import com.tpbl.model.Team;
import com.tpbl.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/draft")
public class DraftController {

    @Autowired
    private DraftService draftService;

    /** 1. 取得當前輪到哪支球隊，若無回傳 204 */
    @GetMapping("/current-team")
    public ResponseEntity<Team> currentTeam() {
        Team team = draftService.currentTeam();
        if (team == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(team);
    }

    /** 2. 取得所有 available 的球員 */
    @GetMapping("/players")
    public List<Player> availablePlayers() {
        return draftService.availablePlayers();
    }

    /** 3. 執行一次指名或放棄，傳 {teamId, playerId}；playerId 為 null 表示放棄 */
    @PostMapping("/pick")
    public Pick makePick(@RequestBody PickRequest req) {
        return draftService.makePick(req.getTeamId(), req.getPlayerId());
    }

    /** 4. 查所有已做過的 pick 紀錄 */
    @GetMapping("/picks")
    public List<Pick> allPicks() {
        return draftService.allPicks();
    }

    // 內部用的 DTO
    public static class PickRequest {
        private Long teamId;
        private Long playerId;

        public Long getTeamId() {
            return teamId;
        }
        public void setTeamId(Long teamId) {
            this.teamId = teamId;
        }

        public Long getPlayerId() {
            return playerId;
        }
        public void setPlayerId(Long playerId) {
            this.playerId = playerId;
        }
    }
}

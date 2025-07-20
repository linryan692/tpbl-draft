// src/main/java/com/tpbl/service/DraftService.java
package com.tpbl.service;

import com.tpbl.model.Pick;
import com.tpbl.model.Player;
import com.tpbl.model.Team;
import com.tpbl.repo.PlayerRepository;
import com.tpbl.repo.PickRepository;
import com.tpbl.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DraftService {

    private final TeamRepository teamRepo;
    private final PlayerRepository playerRepo;
    private final PickRepository pickRepo;

    /** 动态维护的选秀顺序 */
    private List<Team> order = new ArrayList<>();
    private int index = 0;       // 当前队伍在 order 中的下标
    private int round = 1;       // 当前轮次
    private int globalPick = 1;  // 总順位计数

    /**
     * 应用启动后，读取所有 active 的球队，构建选秀顺序
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initAfterStartup() {
        this.order = new ArrayList<>(teamRepo.findByActiveTrueOrderById());
    }

    /** 新增：外面可以拿到当前完整的顺序（用于表头、登录选择等） */
    public List<Team> getTeamOrder() {
        return new ArrayList<>(order);
    }

    /** 新增：外面可以拿到所有已注册的球队（不论 active 与否） */
    public List<Team> getAllTeams() {
        return teamRepo.findAll();
    }

    /**
     * 新增：根据用户名（在 SecurityContext 里）查对应的 Team
     */
    public Team findTeamByUsername(String username) {
        return teamRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Team not found for user: " + username));
    }

    /**
     * 获取当前轮到的球队；如果已结束返回 null
     */
    public Team currentTeam() {
        if (order.isEmpty()) {
            return null;
        }
        return order.get(index);
    }

    /**
     * 返回所有可选（available = true）的球员列表
     */
    public List<Player> availablePlayers() {
        return playerRepo.findByAvailableTrue();
    }

    /**
     * 执行一次选秀动作：
     *  - teamId 指名 playerId；
     *  - playerId == null 视为放弃，本次不指名任何球员，并将该队从后续顺序中移除；
     *  - 否则标记该球员为已选 (available=false)。
     *
     * 保存 Pick 记录后，如果完成了一轮（index >= order.size），则进入下一轮。
     */
    public Pick makePick(Long teamId, Long playerId) {
        // 1) 找到球队
        Team team = teamRepo.findById(teamId)
            .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));

        // 2) 组装 Pick 对象
        Pick pick = new Pick();
        pick.setPickNumber(globalPick++);
        pick.setRound(round);
        pick.setTeam(team);
        pick.setTimestamp(LocalDateTime.now());

        if (playerId == null) {
            // 放弃
            pick.setPlayer(null);
            // 从 order 中移除这支队伍
            order.removeIf(t -> t.getId().equals(teamId));
            // index 不变
        } else {
            // 指名球员
            Player player = playerRepo.findById(playerId)
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerId));
            player.setAvailable(false);
            playerRepo.save(player);

            pick.setPlayer(player);
            // 轮到下一队
            index++;
        }

        // 3) 存库
        pickRepo.save(pick);

        // 4) 如果走完一轮，重置 index 并轮次＋1
        if (!order.isEmpty() && index >= order.size()) {
            index = 0;
            round++;
        }

        return pick;
    }

    /**
     * 返回所有已做的 Pick，按轮次 & 总順位升序
     */
    public List<Pick> allPicks() {
        return pickRepo.findAllByOrderByRoundAscPickNumberAsc();
    }

    /**
     * 如果 order 为空，说明所有队伍都放弃或选秀结束
     */
    public boolean isDraftEnded() {
        return order.isEmpty();
    }
}

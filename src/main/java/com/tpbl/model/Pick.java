// src/main/java/com/tpbl/model/Pick.java
package com.tpbl.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Pick {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 本次指名的全局序号 */
    private int pickNumber;

    /** 轮次 */
    private int round;

    /** 指名/放弃的时间戳 */
    private LocalDateTime timestamp;

    /** 本次指名的队伍 */
    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    /** 本次指名的球员；放弃时为 null */
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    public Pick() {
    }

    // ==== getters & setters ====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPickNumber() {
        return pickNumber;
    }

    public void setPickNumber(int pickNumber) {
        this.pickNumber = pickNumber;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}

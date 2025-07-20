package com.tpbl.model;

import jakarta.persistence.*;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String position;
    private boolean available = true;

    // 新增：最後所屬球隊
    private String lastTeam;

    // 無參建構子
    public Player() {
    }

    // 全參建構子
    public Player(Long id, String name, String position, boolean available, String lastTeam) {
        this.id        = id;
        this.name      = name;
        this.position  = position;
        this.available = available;
        this.lastTeam  = lastTeam;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getLastTeam() {
        return lastTeam;
    }
    public void setLastTeam(String lastTeam) {
        this.lastTeam = lastTeam;
    }
}

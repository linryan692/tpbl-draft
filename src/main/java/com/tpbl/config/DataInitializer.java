// src/main/java/com/tpbl/config/DataInitializer.java
package com.tpbl.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpbl.model.Player;
import com.tpbl.model.Team;
import com.tpbl.repo.PlayerRepository;
import com.tpbl.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final TeamRepository teamRepo;
    private final PlayerRepository playerRepo;
    private final PasswordEncoder passwordEncoder;  // 注入 Spring Security 的 PasswordEncoder

    @Override
    public void run(String... args) throws Exception {
        // —— (1) 初始化 8 支球团，并为每支队生成 username/password —— 
        //    这里直接用拼音做 username，初始密码都设成 “1234”
        String[][] teams = {
            {"崇越隼鷹", "senya"}, 
            {"Lamigo桃猿", "lamigo"},
            {"國泰犀牛", "rhinox"},
            {"遠東獵狐", "farfox"},
            {"華碩天際龍", "dragon"},
            {"三商虎", "tiger"},
            {"富邦悍將", "fubon"},
            {"統一獅", "uni_lion"}
        };
        for (String[] t : teams) {
            String name = t[0], username = t[1];
            if (teamRepo.countByName(name) == 0) {
                String rawPassword = "1234";
                String encoded = passwordEncoder.encode(rawPassword);
                Team team = new Team(null, name, true, username, encoded);
                teamRepo.save(team);
            }
        }

        // —— (2) 批量加载 players-pool.json 其余逻辑不变 —— 
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("players-pool.json");
        try (InputStream in = resource.getInputStream()) {
            List<Map<String,String>> raw = mapper.readValue(
                in, new TypeReference<>(){}
            );
            for (Map<String,String> entry : raw) {
                String pname     = entry.get("name");
                String pos       = entry.get("position");
                String lastTeam  = entry.get("lastTeam");
                if (!playerRepo.existsByName(pname)) {
                    Player p = new Player(null, pname, pos, true, lastTeam);
                    playerRepo.save(p);
                }
            }
        }
    }
}

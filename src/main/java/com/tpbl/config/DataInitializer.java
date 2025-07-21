// src/main/java/com/tpbl/config/DataInitializer.java
package com.tpbl.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpbl.model.Player;
import com.tpbl.model.Team;
import com.tpbl.repo.PlayerRepository;
import com.tpbl.repo.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.InputStream;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(TeamRepository teamRepo,
                                      PlayerRepository playerRepo,
                                      PasswordEncoder encoder) {
        return args -> {
            // 1) 建立 8 支球隊，密碼都是 "password"
            String[][] teams = {
                { "崇越隼鷹", "cy"  },
                { "Lamigo桃猿", "lm"  },
                { "國泰犀牛", "ct"  },
                { "遠東獵狐", "yd"  },
                { "華碩天際龍", "asus"},
                { "三商虎",   "ss"  },
                { "富邦悍將", "fb"  },
                { "統一獅",   "uni" }
            };
            for (var t : teams) {
                String name     = t[0];
                String username = t[1];
                if (teamRepo.countByUsername(username) == 0) {
                    Team team = new Team(
                        null,
                        name,
                        true,
                        username,
                        encoder.encode("password")
                    );
                    teamRepo.save(team);
                }
            }

            // 2) 載入 players-pool.json
            if (playerRepo.count() == 0) {
                ObjectMapper mapper = new ObjectMapper();
                ClassPathResource resource = new ClassPathResource("players-pool.json");
                try (InputStream is = resource.getInputStream()) {
                    List<Player> players = mapper.readValue(is, new TypeReference<>(){});
                    players.forEach(p -> {
                        p.setAvailable(true);
                        // JSON 裡如果有 lastTeam、position、name 欄位都會自動對映
                    });
                    playerRepo.saveAll(players);
                    System.out.println(">> 已載入 " + players.size() + " 名球員到資料庫");
                }
            }
        };
    }
}
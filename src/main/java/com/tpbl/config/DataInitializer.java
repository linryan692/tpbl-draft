// src/main/java/com/tpbl/config/DataInitializer.java
package com.tpbl.config;

import com.tpbl.model.Player;
import com.tpbl.model.Team;
import com.tpbl.repo.PlayerRepository;
import com.tpbl.repo.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(TeamRepository teamRepo,
                               PlayerRepository playerRepo,
                               PasswordEncoder encoder) {
        return args -> {
            // 8 支球隊
            String[][] teams = {
                { "崇越隼鷹", "cy" },
                { "Lamigo桃猿", "lm" },
                { "國泰犀牛", "ct" },
                { "遠東獵狐", "yd" },
                { "華碩天際龍", "asus" },
                { "三商虎", "ss" },
                { "富邦悍將", "fb" },
                { "統一獅", "uni" }
            };
            for (var t : teams) {
                String name = t[0], username = t[1];
                if (teamRepo.countByUsername(username)==0) {
                    String pwd = encoder.encode("password");
                    teamRepo.save(new Team(null, true, name, username, pwd));
                }
            }
            // players-pool.json 載入略……
        };
    }
}

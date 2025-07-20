// src/main/java/com/tpbl/config/TeamUserDetailsService.java
package com.tpbl.config;

import com.tpbl.repo.TeamRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class TeamUserDetailsService implements UserDetailsService {
    private final TeamRepository repo;
    public TeamUserDetailsService(TeamRepository repo) { this.repo = repo; }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return repo.findByUsername(username)
            .map(t -> User.withUsername(t.getUsername())
                          .password(t.getPassword())
                          .roles("TEAM")
                          .build())
            .orElseThrow(() -> new UsernameNotFoundException("找不到隊伍："+username));
    }
}

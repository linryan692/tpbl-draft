package com.tpbl.config;

import com.tpbl.model.Team;
import com.tpbl.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamUserDetailsService implements UserDetailsService {
    private final TeamRepository teamRepo;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Team team = teamRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Team not found: " + username));
        return new TeamUserDetails(team);
    }
}
package com.tpbl.config;

import com.tpbl.model.Team;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@Getter
public class TeamUserDetails implements UserDetails {
    private final Team team;

    public TeamUserDetails(Team team) {
        this.team = team;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 如果不需要角色，可回空集合
        return java.util.Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return team.getPassword();
    }

    @Override
    public String getUsername() {
        return team.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return team.isActive();
    }
}
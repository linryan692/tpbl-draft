// src/main/java/com/tpbl/model/Team.java
package com.tpbl.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "team", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team implements UserDetails {
    @Id @GeneratedValue
    private Long id;
    private boolean active;
    private String name;
    private String password;
    private String username;

    // 方便 DataInitializer 調用
    public Team(Long id, String name, boolean active, String username, String password) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.username = username;
        this.password = password;
    }

    // --- UserDetails methods ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_USER");
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return active; }

    @Override
    public boolean isAccountNonLocked() { return active; }

    @Override
    public boolean isCredentialsNonExpired() { return active; }

    @Override
    public boolean isEnabled() { return active; }
}

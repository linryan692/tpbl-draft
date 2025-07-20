package com.tpbl.config;

import com.tpbl.model.Team;
import com.tpbl.repo.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TeamRepository teamRepo;

    // 1) 密碼編碼器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2) 讀取 Team 來當 UserDetails
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Team team = teamRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            // 這裡把 Team 資料轉成 Spring Security 的 UserDetails
            return User.builder()
                    .username(team.getUsername())
                    .password(team.getPassword())
                    .roles("TEAM")
                    .build();
        };
    }

    // 3) SecurityFilterChain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          // 3a) 靜態資源、登入頁、WS endpoint、API（如果需要）全部放行
          .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/index.html", "/", "/login**", "/css/**", "/js/**",
                "/ws/**", "/api/**", "/h2-console/**"
            ).permitAll()
            .anyRequest().authenticated()
          )
          // 3b) form-login 設定
          .formLogin(form -> form
            .loginPage("/login")       // 自己做的 login.html
            .defaultSuccessUrl("/index.html", true)
            .permitAll()
          )
          // 3c) logout 放行
          .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .permitAll()
          )
          // 3d) 為了 H2 console & WebSocket 關閉 CSRF
          .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**", "/ws/**")
          )
          // 3e) 允許 frame 同源 (H2 console)
          .headers(headers -> headers
            .frameOptions(frame -> frame.sameOrigin())
          )
        ;
        return http.build();
    }

    // 4) 如果你要用 @Autowired AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}

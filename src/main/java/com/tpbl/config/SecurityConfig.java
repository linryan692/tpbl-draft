package com.tpbl.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.tpbl.config.TeamUserDetailsService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final TeamUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          // 允許 H2-console frame、同源
          .headers(headers -> headers.frameOptions().sameOrigin())
          .csrf(csrf -> csrf
            // 忽略 H2-console 跟 WebSocket 握手的 CSRF
            .ignoringRequestMatchers("/h2-console/**", "/ws/**")
          )
          .authorizeHttpRequests(auth -> auth
            // 放行靜態資源、login 頁、H2-console、WebSocket handshake
            .requestMatchers(
               "/login", "/h2-console/**", "/css/**", "/js/**",
               "/ws/**"
            ).permitAll()
            // 其餘都必須認證
            .anyRequest().authenticated()
          )
          .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
          )
          .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(BCryptPasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
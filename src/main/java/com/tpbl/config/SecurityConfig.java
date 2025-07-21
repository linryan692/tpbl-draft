package com.tpbl.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final TeamUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, BCryptPasswordEncoder encoder) throws Exception {
        // <-- 把 authProvider 加進來
        http
          .authenticationProvider(authProvider(encoder))
          // 先关闭 H2 控制台的 frame 同源策略限制
          .headers(headers -> headers.frameOptions().sameOrigin())
          .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/**")
          )
          .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/h2-console/**", "/css/**", "/js/**").permitAll()
            .anyRequest().authenticated()
          )
          .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/", true)
            .permitAll()
          )
          .logout(logout -> logout
            .permitAll()
          );

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
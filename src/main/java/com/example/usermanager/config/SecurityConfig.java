package com.example.usermanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().sameOrigin() // Para H2 Console
                .and()
                .authorizeRequests()
                .antMatchers("/h2-console/**", "/api/sign-up", "/api/login").permitAll()
                .anyRequest().authenticated();

        return http.build();
    }
}
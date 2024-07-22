package com.mikegambino.clinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration{
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(request -> request
                .requestMatchers("/signup", "/logout").permitAll()
                .requestMatchers("resources/**", "/css/**").permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin(formLogin -> formLogin
                .loginPage("/login").permitAll()
                .passwordParameter("password")
                .usernameParameter("username")
                .defaultSuccessUrl("/account", true)
        );

        return http.build();
    }
}
package com.mikegambino.clinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration{
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{

        httpSecurity
                .authorizeRequests()
                .requestMatchers("/signup").permitAll()
                .requestMatchers("resources/**", "/css/**").permitAll()
                .requestMatchers("/WEB-INF/**").permitAll()
                .anyRequest().authenticated();

        httpSecurity
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .passwordParameter("password")
                .usernameParameter("username")
                .defaultSuccessUrl("/account", true);

        httpSecurity.logout().permitAll();

        httpSecurity
                .csrf()
                .disable();

        return httpSecurity.build();
    }
}
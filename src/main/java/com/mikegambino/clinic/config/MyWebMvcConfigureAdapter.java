package com.mikegambino.clinic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableJpaRepositories("com.mikegambino.clinic.repository")
public class MyWebMvcConfigureAdapter implements WebMvcConfigurer {
}

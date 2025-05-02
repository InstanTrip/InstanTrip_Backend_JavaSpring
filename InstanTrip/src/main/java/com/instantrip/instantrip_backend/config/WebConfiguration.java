package com.instantrip.instantrip_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * In this class, we tell SpringBoot to render and return our Thymeleaf html document.
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addViewController("/").setViewName("home");
    registry.addViewController("/login").setViewName("login");
    registry.addViewController("/dashboard").setViewName("dashboard");
  }
}
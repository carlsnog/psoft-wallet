package com.ufcg.psoft.commerce.config;

import com.ufcg.psoft.commerce.service.interesse.NotificacaoServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerConfig {

  @Bean
  public Logger notificacaoLogger() {
    return LoggerFactory.getLogger(NotificacaoServiceImpl.class);
  }
}

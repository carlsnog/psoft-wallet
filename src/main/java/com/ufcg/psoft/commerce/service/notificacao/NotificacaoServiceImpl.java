package com.ufcg.psoft.commerce.service.notificacao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.commerce.model.Cliente;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificacaoServiceImpl implements NotificacaoService {
  private final ObjectMapper objectMapper;

  public NotificacaoServiceImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public void notificar(Cliente cliente, String mensagem) {

    var data =
        Notificacao.builder()
            .userId(cliente.getId())
            .mensagem("Ola " + cliente.getNome() + ", " + mensagem)
            .timestamp(LocalDateTime.now())
            .build();

    try {
      var json = objectMapper.writeValueAsString(data);
      log.info(json);
    } catch (JsonProcessingException e) {
      log.error("Erro ao enviar mensagem", e);
    }
  }
}

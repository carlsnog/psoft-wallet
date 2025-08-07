package com.ufcg.psoft.commerce.service.interesse;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Builder
@AllArgsConstructor
class NotificacaoEstruturada {
  @JsonProperty("mensagem")
  private String mensagem;

  @JsonProperty("userId")
  private long userId;

  @JsonProperty("timestamp")
  private LocalDateTime timestamp;
}

@Service
public class NotificacaoServiceImpl implements NotificacaoService {
  private final Logger logger;
  private final InteresseRepository interesseRepository;
  private final InteresseService interesseService;
  private final ObjectMapper objectMapper;

  public NotificacaoServiceImpl(
      InteresseRepository interesseRepository,
      InteresseService interesseService,
      ObjectMapper objectMapper,
      Logger logger) {
    this.interesseRepository = interesseRepository;
    this.interesseService = interesseService;
    this.objectMapper = objectMapper;
    this.logger = logger;
  }

  @Override
  public void notificarAlteracaoPreco(Ativo ativo, BigDecimal novoPreco) {
    notificar(
        TipoInteresseEnum.PRECO,
        ativo,
        "o preço do ativo " + ativo.getNome() + " foi alterado para " + novoPreco + "!");
  }

  @Override
  public void notificarDisponibilidade(Ativo ativo) {
    notificar(
        TipoInteresseEnum.DISPONIBILIDADE,
        ativo,
        "o ativo " + ativo.getNome() + " esta disponivel para compra!",
        true);
  }

  private void notificar(TipoInteresseEnum tipo, Ativo ativo, String msg) {
    notificar(tipo, ativo, msg, false);
  }

  private void notificar(
      TipoInteresseEnum tipo, Ativo ativo, String msg, boolean deletarAposNotificar) {
    var interesses = interesseRepository.findByTipoAndAtivo_Id(tipo, ativo.getId());

    for (Interesse interesse : interesses) {
      var msgFormatada = formatarMensagem(interesse.getCliente(), msg);
      send(msgFormatada);

      if (deletarAposNotificar) {
        interesseService.remover(interesse.getId(), Admin.getInstance());
      }
    }
  }

  private NotificacaoEstruturada formatarMensagem(Cliente cliente, String msg) {
    return NotificacaoEstruturada.builder()
        .mensagem("Ola " + cliente.getNome() + ", " + msg)
        .userId(cliente.getId())
        .timestamp(LocalDateTime.now())
        .build();
  }

  private void send(NotificacaoEstruturada msg) {
    try {
      var json = objectMapper.writeValueAsString(msg);
      logger.info(json);
    } catch (JsonProcessingException e) {
      logger.error("Erro ao enviar mensagem", e);
    }
  }
}

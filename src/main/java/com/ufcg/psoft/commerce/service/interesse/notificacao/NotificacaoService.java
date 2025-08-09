package com.ufcg.psoft.commerce.service.interesse.notificacao;

import com.ufcg.psoft.commerce.model.Cliente;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
class Notificacao {
  private final long userId;
  private final String mensagem;
  private final LocalDateTime timestamp;
}

public interface NotificacaoService {

  public void notificar(Cliente cliente, String mensagem);
}

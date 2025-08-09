package com.ufcg.psoft.commerce.service.interesse.listeners.preco;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.service.interesse.listeners.AtivoBaseEvent;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class AtivoCotacaoEvent extends AtivoBaseEvent {
  private static final long serialVersionUID = 1L;

  private BigDecimal novaCotacao;

  public AtivoCotacaoEvent(Object source, Ativo ativo, BigDecimal novaCotacao) {
    super(source, ativo);
    this.novaCotacao = novaCotacao;
  }
}

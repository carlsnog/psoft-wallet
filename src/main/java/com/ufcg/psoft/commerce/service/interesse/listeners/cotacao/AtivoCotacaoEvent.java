package com.ufcg.psoft.commerce.service.interesse.listeners.cotacao;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.service.interesse.listeners.AtivoBaseEvent;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class AtivoCotacaoEvent extends AtivoBaseEvent {
  private static final long serialVersionUID = 1L;

  private final BigDecimal cotacaoAntiga;
  private final BigDecimal novaCotacao;

  public AtivoCotacaoEvent(Object source, Ativo ativo, BigDecimal cotacaoAntiga) {
    super(source, ativo);
    this.cotacaoAntiga = cotacaoAntiga;
    this.novaCotacao = ativo.getCotacao();
  }
}

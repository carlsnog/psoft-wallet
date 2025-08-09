package com.ufcg.psoft.commerce.service.interesse.listeners.preco;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.service.interesse.listeners.AtivoBaseEvent;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class AtivoPrecoEvent extends AtivoBaseEvent {
  private static final long serialVersionUID = 1L;

  private BigDecimal novoPreco;

  public AtivoPrecoEvent(Object source, Ativo ativo, BigDecimal novoPreco) {
    super(source, ativo);
    this.novoPreco = novoPreco;
  }
}

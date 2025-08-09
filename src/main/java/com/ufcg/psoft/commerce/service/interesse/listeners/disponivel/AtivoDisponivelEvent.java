package com.ufcg.psoft.commerce.service.interesse.listeners.disponivel;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.service.interesse.listeners.AtivoBaseEvent;
import lombok.Getter;

@Getter
public class AtivoDisponivelEvent extends AtivoBaseEvent {
  public AtivoDisponivelEvent(Object source, Ativo ativo) {
    super(source, ativo);
  }
}

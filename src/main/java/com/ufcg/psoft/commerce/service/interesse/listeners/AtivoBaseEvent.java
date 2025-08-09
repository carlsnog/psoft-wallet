package com.ufcg.psoft.commerce.service.interesse.listeners;

import com.ufcg.psoft.commerce.model.Ativo;
import org.springframework.context.ApplicationEvent;

public abstract class AtivoBaseEvent extends ApplicationEvent {
  private final Ativo ativo;

  public AtivoBaseEvent(Object source, Ativo ativo) {
    super(source);
    this.ativo = ativo;
  }

  public Ativo getAtivo() {
    return ativo;
  }
}

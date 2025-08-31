package com.ufcg.psoft.commerce.service.compra.listeners.liberada;

// CompraDisponivelEvent.java
import com.ufcg.psoft.commerce.model.compra.Compra;
import com.ufcg.psoft.commerce.service.compra.listeners.CompraBaseEvent;
import lombok.Getter;

@Getter
public class CompraLiberadaEvent extends CompraBaseEvent {
  private static final long serialVersionUID = 1L;

  public CompraLiberadaEvent(Object source, Compra compra) {
    super(source, compra);
  }
}

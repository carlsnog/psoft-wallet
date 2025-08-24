package com.ufcg.psoft.commerce.service.compra.listeners;

// CompraBaseEvent.java
import com.ufcg.psoft.commerce.model.compra.Compra;
import org.springframework.context.ApplicationEvent;

@SuppressWarnings("serial")
public abstract class CompraBaseEvent extends ApplicationEvent {
  private static final long serialVersionUID = 1L;

  private final transient Compra compra;

  public CompraBaseEvent(Object source, Compra compra) {
    super(source);
    this.compra = compra;
  }

  public Compra getCompra() {
    return compra;
  }
}

package com.ufcg.psoft.commerce.enums;

import com.ufcg.psoft.commerce.model.compra.Compra;
import com.ufcg.psoft.commerce.model.compra.state.CompraState;
import com.ufcg.psoft.commerce.model.compra.state.CompradoState;
import com.ufcg.psoft.commerce.model.compra.state.DisponivelState;
import com.ufcg.psoft.commerce.model.compra.state.EmCarteiraState;
import com.ufcg.psoft.commerce.model.compra.state.SolicitadoState;
import java.util.function.Function;

public enum CompraStatusEnum {
  SOLICITADO((Compra compra) -> new SolicitadoState(compra)),
  DISPONIVEL((Compra compra) -> new DisponivelState(compra)),
  COMPRADO((Compra compra) -> new CompradoState(compra)),
  EM_CARTEIRA((Compra compra) -> new EmCarteiraState(compra));

  private final Function<Compra, CompraState> factory;

  CompraStatusEnum(Function<Compra, CompraState> factory) {
    this.factory = factory;
  }

  public CompraState getState(Compra compra) {
    return factory.apply(compra);
  }
}

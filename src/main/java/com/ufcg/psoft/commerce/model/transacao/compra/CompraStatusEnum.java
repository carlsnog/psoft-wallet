package com.ufcg.psoft.commerce.model.transacao.compra;

import com.ufcg.psoft.commerce.model.transacao.TransacaoStateEnum;
import com.ufcg.psoft.commerce.model.transacao.compra.state.CompraState;
import com.ufcg.psoft.commerce.model.transacao.compra.state.CompradoState;
import com.ufcg.psoft.commerce.model.transacao.compra.state.DisponivelState;
import com.ufcg.psoft.commerce.model.transacao.compra.state.EmCarteiraState;
import com.ufcg.psoft.commerce.model.transacao.compra.state.SolicitadoState;
import java.util.function.Function;

public enum CompraStatusEnum implements TransacaoStateEnum<Compra> {
  SOLICITADO((Compra compra) -> new SolicitadoState(compra)),
  DISPONIVEL((Compra compra) -> new DisponivelState(compra)),
  COMPRADO((Compra compra) -> new CompradoState(compra)),
  EM_CARTEIRA((Compra compra) -> new EmCarteiraState(compra));

  private final Function<Compra, CompraState> factory;

  CompraStatusEnum(Function<Compra, CompraState> factory) {
    this.factory = factory;
  }

  @Override
  public CompraState getState(Compra compra) {
    return factory.apply(compra);
  }
}

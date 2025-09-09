package com.ufcg.psoft.commerce.model.transacao.resgate;

import com.ufcg.psoft.commerce.model.transacao.TransacaoStateEnum;
import com.ufcg.psoft.commerce.model.transacao.resgate.state.ConfirmadoResgateState;
import com.ufcg.psoft.commerce.model.transacao.resgate.state.EmContaResgateState;
import com.ufcg.psoft.commerce.model.transacao.resgate.state.ResgateState;
import com.ufcg.psoft.commerce.model.transacao.resgate.state.SolicitadoResgateState;
import java.util.function.Function;

public enum ResgateStatusEnum implements TransacaoStateEnum<Resgate> {
  SOLICITADO((Resgate resgate) -> new SolicitadoResgateState(resgate)),
  CONFIRMADO((Resgate resgate) -> new ConfirmadoResgateState(resgate)),
  EM_CONTA((Resgate resgate) -> new EmContaResgateState(resgate));

  private final Function<Resgate, ResgateState> factory;

  ResgateStatusEnum(Function<Resgate, ResgateState> factory) {
    this.factory = factory;
  }

  @Override
  public ResgateState getState(Resgate resgate) {
    return factory.apply(resgate);
  }
}

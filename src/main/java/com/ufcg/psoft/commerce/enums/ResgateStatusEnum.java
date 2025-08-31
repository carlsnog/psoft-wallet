package com.ufcg.psoft.commerce.enums;

import com.ufcg.psoft.commerce.model.resgate.Resgate;
import com.ufcg.psoft.commerce.model.resgate.state.ConfirmadoResgateState;
import com.ufcg.psoft.commerce.model.resgate.state.EmContaResgateState;
import com.ufcg.psoft.commerce.model.resgate.state.ResgateState;
import com.ufcg.psoft.commerce.model.resgate.state.SolicitadoResgateState;
import java.util.function.Function;

public enum ResgateStatusEnum {
  SOLICITADO((Resgate resgate) -> new SolicitadoResgateState(resgate)),
  CONFIRMADO((Resgate resgate) -> new ConfirmadoResgateState(resgate)),
  EM_CONTA((Resgate resgate) -> new EmContaResgateState(resgate));

  private final Function<Resgate, ResgateState> factory;

  ResgateStatusEnum(Function<Resgate, ResgateState> factory) {
    this.factory = factory;
  }

  public ResgateState getState(Resgate resgate) {
    return factory.apply(resgate);
  }
}

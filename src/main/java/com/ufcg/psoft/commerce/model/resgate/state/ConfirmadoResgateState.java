package com.ufcg.psoft.commerce.model.resgate.state;

import com.ufcg.psoft.commerce.enums.ResgateStatusEnum;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.resgate.Resgate;

public class ConfirmadoResgateState extends ResgateState {
  public ConfirmadoResgateState(Resgate resgate) {
    super(resgate);
  }

  @Override
  public void confirmar(Usuario usuario) {
    setStatus(ResgateStatusEnum.EM_CONTA);
  }

  @Override
  public boolean deveFinalizar() {
    return true;
  }
}

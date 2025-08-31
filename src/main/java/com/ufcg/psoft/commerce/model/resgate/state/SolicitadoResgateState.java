package com.ufcg.psoft.commerce.model.resgate.state;

import com.ufcg.psoft.commerce.enums.ResgateStatusEnum;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.resgate.Resgate;

public class SolicitadoResgateState extends ResgateState {
  public SolicitadoResgateState(Resgate resgate) {
    super(resgate);
  }

  @Override
  public void confirmar(Usuario usuario) {
    preValidarAdmin(usuario);
    preValidarSaldo();
    setStatus(ResgateStatusEnum.CONFIRMADO);
  }
}

package com.ufcg.psoft.commerce.model.transacao.resgate.state;

import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import com.ufcg.psoft.commerce.model.transacao.resgate.ResgateStatusEnum;

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

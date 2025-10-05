package com.ufcg.psoft.commerce.model.transacao.resgate.state;

import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;

public class EmContaResgateState extends ResgateState {
  public EmContaResgateState(Resgate resgate) {
    super(resgate);
  }

  @Override
  public void confirmar(Usuario usuario) {
    throw new CommerceException(ErrorCode.RESGATE_JA_ESTA_EM_CONTA);
  }
}

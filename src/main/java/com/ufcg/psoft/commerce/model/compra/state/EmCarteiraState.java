package com.ufcg.psoft.commerce.model.compra.state;

import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.compra.Compra;

public class EmCarteiraState extends CompraState {

  public EmCarteiraState(Compra compra) {
    super(compra);
  }

  @Override
  public void confirmar(Usuario usuario) {
    throw new CommerceException(ErrorCode.CONFIRMANDO_COMPRA_FINALIZADA);
  }
}

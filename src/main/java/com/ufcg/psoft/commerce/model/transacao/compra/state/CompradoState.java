package com.ufcg.psoft.commerce.model.transacao.compra.state;

import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.compra.CompraStatusEnum;

public class CompradoState extends CompraState {

  public CompradoState(Compra compra) {
    super(compra);
  }

  @Override
  public boolean deveFinalizar() {
    return true;
  }

  @Override
  public void confirmar(Usuario usuario) {
    preValidarAtivo();
    setStatus(CompraStatusEnum.EM_CARTEIRA);
  }
}

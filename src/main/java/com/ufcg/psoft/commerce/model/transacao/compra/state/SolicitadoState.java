package com.ufcg.psoft.commerce.model.transacao.compra.state;

import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.compra.CompraStatusEnum;

public class SolicitadoState extends CompraState {
  public SolicitadoState(Compra compra) {
    super(compra);
  }

  @Override
  public void confirmar(Usuario usuario) {
    preValidarAtivo();
    preValidarAdmin(usuario);

    setStatus(CompraStatusEnum.DISPONIVEL);
  }
}

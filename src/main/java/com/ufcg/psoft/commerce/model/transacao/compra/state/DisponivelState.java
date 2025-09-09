package com.ufcg.psoft.commerce.model.transacao.compra.state;

import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.compra.CompraStatusEnum;

public class DisponivelState extends CompraState {

  public DisponivelState(Compra compra) {
    super(compra);
  }

  @Override
  public void confirmar(Usuario usuario) {
    preValidarAtivo();
    preValidarCliente(usuario);

    setStatus(CompraStatusEnum.COMPRADO);
  }
}

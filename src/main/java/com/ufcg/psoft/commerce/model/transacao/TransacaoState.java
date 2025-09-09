package com.ufcg.psoft.commerce.model.transacao;

import com.ufcg.psoft.commerce.model.Usuario;

public interface TransacaoState {
  void confirmar(Usuario usuario);

  boolean deveFinalizar();
}

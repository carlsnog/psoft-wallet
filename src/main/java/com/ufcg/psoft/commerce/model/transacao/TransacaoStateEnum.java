package com.ufcg.psoft.commerce.model.transacao;

public interface TransacaoStateEnum<T extends Transacao> {
  TransacaoState getState(T transacao);
}

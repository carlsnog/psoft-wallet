package com.ufcg.psoft.commerce.service.interesse;

import com.ufcg.psoft.commerce.model.Ativo;
import java.math.BigDecimal;

public interface NotificacaoService {

  public void notificarAlteracaoPreco(Ativo ativo, BigDecimal novoPreco);

  public void notificarDisponibilidade(Ativo ativo);
}

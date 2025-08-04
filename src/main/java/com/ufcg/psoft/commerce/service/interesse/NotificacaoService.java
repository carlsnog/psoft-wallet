package com.ufcg.psoft.commerce.service.interesse;

import java.math.BigDecimal;

import com.ufcg.psoft.commerce.model.Ativo;

public interface NotificacaoService {

    public void notificarAlteracaoPreco(Ativo ativo, BigDecimal novoPreco);

    public void notificarDisponibilidade(Ativo ativo);

}
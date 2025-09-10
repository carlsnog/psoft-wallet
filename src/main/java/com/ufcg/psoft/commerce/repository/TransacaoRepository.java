package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.transacao.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {}

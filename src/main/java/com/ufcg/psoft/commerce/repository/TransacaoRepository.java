package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.transacao.Transacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
  List<Transacao> findAllByCliente_Id(Long clienteId);
}

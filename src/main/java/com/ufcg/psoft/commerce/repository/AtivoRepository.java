package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Ativo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtivoRepository extends JpaRepository<Ativo, Long> {
  boolean existsByNome(String nome);

  boolean existsByNomeAndIdNot(String nome, Long id);
}

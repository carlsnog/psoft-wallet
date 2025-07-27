package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.model.Ativo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtivoRepository extends JpaRepository<Ativo, Long> {
  boolean existsByNome(String nome);

  boolean existsByNomeAndIdNot(String nome, Long id);

  List<Ativo> findAllByTipo(AtivoTipo tipo);
}

package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.model.Cliente;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
  List<Cliente> findByNomeContainingIgnoreCase(String nome);

  List<Cliente> findByPlano(PlanoEnum plano);
}

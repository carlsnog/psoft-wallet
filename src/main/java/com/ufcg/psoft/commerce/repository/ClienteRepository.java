
package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.PlanoEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    List<Cliente> findByPlano(PlanoEnum plano);

    Optional<Cliente> findByCodigoAcesso(String codigoAcesso);
}

package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface CompraRepository extends JpaRepository<Compra, Long> {

  Optional<Compra> findByIdAndCliente_Id(long id, long clienteId);

  List<Compra> findByCliente_Id(long clienteId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Compra> findById(long id);
}

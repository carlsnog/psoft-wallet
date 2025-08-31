package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.resgate.Resgate;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ResgateRepository extends JpaRepository<Resgate, Long> {

  Optional<Resgate> findByIdAndCliente_Id(long id, long clienteId);

  List<Resgate> findByCliente_Id(long clienteId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Resgate> findById(long id);
}

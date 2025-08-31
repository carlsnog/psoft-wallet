package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.model.AtivoCarteira;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtivoCarteiraRepository extends JpaRepository<AtivoCarteira, Long> {

  List<AtivoCarteira> findAllByCliente_Id(Long clienteId);

  List<AtivoCarteira> findAllByAtivo_Id(Long ativoId);

  List<AtivoCarteira> findAllByCliente_IdAndAtivo_Id(long id, long ativoId);
}

package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.model.Interesse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteresseRepository extends JpaRepository<Interesse, Long> {
  List<Interesse> findByCliente_Id(long clienteId);

  List<Interesse> findByAtivo_Id(long ativoId);

  List<Interesse> findByTipo(TipoInteresseEnum tipo);

  List<Interesse> findByTipoAndAtivo_Id(TipoInteresseEnum tipo, long ativoId);
}

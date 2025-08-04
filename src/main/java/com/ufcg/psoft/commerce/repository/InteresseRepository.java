package com.ufcg.psoft.commerce.repository;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.model.Interesse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteresseRepository extends JpaRepository<Interesse, Long> {
  List<Interesse> findAll();

  List<Interesse> findByClienteId(long clienteId);

  List<Interesse> findByAtivoId(long ativoId);

  List<Interesse> findByTipo(TipoInteresseEnum tipo);

  List<Interesse> findByTipoAndAtivoId(TipoInteresseEnum tipo, long ativoId);
}

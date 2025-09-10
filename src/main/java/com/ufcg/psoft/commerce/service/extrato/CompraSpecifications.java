package com.ufcg.psoft.commerce.service.extrato;

import com.ufcg.psoft.commerce.dto.CompraFilterDTO;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import org.springframework.data.jpa.domain.Specification;

public final class CompraSpecifications
    extends AbstractTransacaoSpecification<Compra, CompraFilterDTO> {

  private CompraSpecifications() {}

  public static Specification<Compra> byFilter(CompraFilterDTO filter) {
    CompraSpecifications spec = new CompraSpecifications();
    return (root, query, cb) -> spec.buildPredicate(filter, root, cb);
  }

  @Override
  protected Object getStatus(CompraFilterDTO filter) {
    return filter.getStatus();
  }

  @Override
  protected Long getAtivoId(CompraFilterDTO filter) {
    return filter.getAtivoId();
  }

  @Override
  protected java.time.LocalDateTime getStartDate(CompraFilterDTO filter) {
    return filter.getStartDate();
  }

  @Override
  protected java.time.LocalDateTime getEndDate(CompraFilterDTO filter) {
    return filter.getEndDate();
  }

  @Override
  protected Long getClienteId(CompraFilterDTO filter) {
    return filter.getClienteId();
  }

  @Override
  protected String getStatusField() {
    return "status";
  }

  @Override
  protected String getAtivoFieldPath() {
    return "ativo.id";
  }

  @Override
  protected String getDataReferenciaField() {
    return "abertaEm";
  }
}

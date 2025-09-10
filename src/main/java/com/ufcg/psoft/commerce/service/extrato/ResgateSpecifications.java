package com.ufcg.psoft.commerce.service.extrato;

import com.ufcg.psoft.commerce.dto.ResgateFilterDTO;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import org.springframework.data.jpa.domain.Specification;

public final class ResgateSpecifications
    extends AbstractTransacaoSpecification<Resgate, ResgateFilterDTO> {

  private ResgateSpecifications() {}

  public static Specification<Resgate> byFilter(ResgateFilterDTO filter) {
    ResgateSpecifications spec = new ResgateSpecifications();
    return (root, query, cb) -> spec.buildPredicate(filter, root, cb);
  }

  @Override
  protected Object getStatus(ResgateFilterDTO filter) {
    return filter.getStatus();
  }

  @Override
  protected Long getAtivoId(ResgateFilterDTO filter) {
    return filter.getAtivoId();
  }

  @Override
  protected java.time.LocalDateTime getStartDate(ResgateFilterDTO filter) {
    return filter.getStartDate();
  }

  @Override
  protected java.time.LocalDateTime getEndDate(ResgateFilterDTO filter) {
    return filter.getEndDate();
  }

  @Override
  protected Long getClienteId(ResgateFilterDTO filter) {
    return filter.getClienteId();
  }

  @Override
  protected String getStatusField() {
    return "status";
  }

  @Override
  protected String getAtivoFieldPath() {
    return "ativoCarteira.ativo.id";
  }

  @Override
  protected String getDataReferenciaField() {
    return "solicitacaoEm";
  }
}

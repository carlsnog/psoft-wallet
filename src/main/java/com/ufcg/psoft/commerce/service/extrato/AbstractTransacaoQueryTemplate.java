package com.ufcg.psoft.commerce.service.extrato;

import java.awt.print.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

public abstract class AbstractTransacaoQueryTemplate<T, F> {
  protected final Pageable pageable;
  protected final F filter;

  protected AbstractTransacaoQueryTemplate(F filter, Pageable pageable) {
    this.filter = filter;
    this.pageable = pageable;
  }

  // template method
  public Page<T> executeTemplate() {
    Specification<?> spec = buildSpecification(filter);
    return executeRepositoryQuery(spec, pageable);
  }

  protected abstract Specification<?> buildSpecification(F filter);

  protected abstract Page<T> executeRepositoryQuery(Specification<?> spec, Pageable pageable);
}

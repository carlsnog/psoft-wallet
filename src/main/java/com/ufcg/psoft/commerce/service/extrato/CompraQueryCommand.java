package com.ufcg.psoft.commerce.service.extrato;

import com.ufcg.psoft.commerce.dto.CompraFilterDTO;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import java.awt.print.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

public class CompraQueryCommand implements QueryCommand<Page<Compra>> {
  private final CompraRepository repo;
  private final CompraFilterDTO filter;
  private final Pageable pageable;

  public CompraQueryCommand(CompraRepository repo, CompraFilterDTO filter, Pageable pageable) {
    this.repo = repo;
    this.filter = filter;
    this.pageable = pageable;
  }

  @Override
  public Page<Compra> execute() {
    Specification<Compra> spec = CompraSpecifications.byFilter(filter);
    return repo.findAll(spec, (org.springframework.data.domain.Pageable) pageable);
  }
}

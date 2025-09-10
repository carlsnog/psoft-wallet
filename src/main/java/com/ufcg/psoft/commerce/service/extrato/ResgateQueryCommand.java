package com.ufcg.psoft.commerce.service.extrato;

import com.ufcg.psoft.commerce.dto.ResgateFilterDTO;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import com.ufcg.psoft.commerce.repository.ResgateRepository;
import java.awt.print.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

public class ResgateQueryCommand implements QueryCommand<Page<Resgate>> {
  private final ResgateRepository repo;
  private final ResgateFilterDTO filter;
  private final Pageable pageable;

  public ResgateQueryCommand(ResgateRepository repo, ResgateFilterDTO filter, Pageable pageable) {
    this.repo = repo;
    this.filter = filter;
    this.pageable = pageable;
  }

  @Override
  public Page<Resgate> execute() {
    Specification<Resgate> spec = ResgateSpecifications.byFilter(filter);
    return repo.findAll(spec, (org.springframework.data.domain.Pageable) pageable);
  }
}

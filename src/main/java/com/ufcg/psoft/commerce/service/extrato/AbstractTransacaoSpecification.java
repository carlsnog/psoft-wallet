package com.ufcg.psoft.commerce.service.extrato;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Classe base para Specifications de Transações (Compra, Resgate, etc). */
public abstract class AbstractTransacaoSpecification<T, F> {

  public Predicate buildPredicate(F filter, Root<T> root, CriteriaBuilder cb) {
    if (filter == null) {
      return cb.conjunction();
    }

    List<Predicate> predicates = new ArrayList<>();

    addStatusPredicate(filter, root, cb, predicates);
    addAtivoPredicate(filter, root, cb, predicates);
    addPeriodoPredicate(filter, root, cb, predicates);
    addClientePredicate(filter, root, cb, predicates);

    return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
  }

  protected abstract Object getStatus(F filter);

  protected abstract Long getAtivoId(F filter);

  protected abstract LocalDateTime getStartDate(F filter);

  protected abstract LocalDateTime getEndDate(F filter);

  protected abstract Long getClienteId(F filter);

  protected abstract String getStatusField();

  protected abstract String getAtivoFieldPath();

  protected abstract String getDataReferenciaField();

  private void addStatusPredicate(
      F filter, Root<T> root, CriteriaBuilder cb, List<Predicate> predicates) {
    var status = getStatus(filter);
    if (status != null) {
      predicates.add(cb.equal(root.get(getStatusField()), status));
    }
  }

  private void addAtivoPredicate(
      F filter, Root<T> root, CriteriaBuilder cb, List<Predicate> predicates) {
    var ativoId = getAtivoId(filter);
    if (ativoId != null) {
      // suporta path encadeado (ex: ativoCarteira.ativo.id)
      String[] pathParts = getAtivoFieldPath().split("\\.");
      var path = root.get(pathParts[0]);
      for (int i = 1; i < pathParts.length; i++) {
        path = path.get(pathParts[i]);
      }
      predicates.add(cb.equal(path, ativoId));
    }
  }

  private void addPeriodoPredicate(
      F filter, Root<T> root, CriteriaBuilder cb, List<Predicate> predicates) {
    var start = getStartDate(filter);
    var end = getEndDate(filter);
    if (start != null) {
      predicates.add(cb.greaterThanOrEqualTo(root.get(getDataReferenciaField()), start));
    }
    if (end != null) {
      predicates.add(cb.lessThanOrEqualTo(root.get(getDataReferenciaField()), end));
    }
  }

  private void addClientePredicate(
      F filter, Root<T> root, CriteriaBuilder cb, List<Predicate> predicates) {
    var clienteId = getClienteId(filter);
    if (clienteId != null) {
      predicates.add(cb.equal(root.get("cliente").get("id"), clienteId));
    }
  }
}

package com.ufcg.psoft.commerce.model;

import jakarta.persistence.Entity;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class Acao extends Ativo {

  @Override
  public BigDecimal calcularImposto(BigDecimal lucro) {
    if (lucro.signum() <= 0) return BigDecimal.ZERO;
    return lucro.multiply(new BigDecimal("0.15"));
  }
}

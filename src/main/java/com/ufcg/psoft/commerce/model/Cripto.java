package com.ufcg.psoft.commerce.model;

import jakarta.persistence.Entity;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class Cripto extends Ativo {
  @Override
  public BigDecimal calcularImposto(BigDecimal lucro) {
    if (lucro.signum() <= 0) return BigDecimal.ZERO;

    BigDecimal teto = new BigDecimal("5000");
    if (lucro.compareTo(teto) <= 0) {
      return lucro.multiply(new BigDecimal("0.15"));
    }
    return lucro.multiply(new BigDecimal("0.225"));
  }
}

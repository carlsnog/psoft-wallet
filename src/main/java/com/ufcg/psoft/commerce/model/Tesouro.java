package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
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
public class Tesouro extends Ativo {
  @Override
  public void atualizarCotacao(BigDecimal novaCotacao) {
    throw new CommerceException(ErrorCode.OPERACAO_INVALIDA_PARA_O_TIPO);
  }

  @Override
  public BigDecimal calcularImposto(BigDecimal lucro) {
    if (lucro.signum() <= 0) return BigDecimal.ZERO;
    return lucro.multiply(new BigDecimal("0.10"));
  }
}

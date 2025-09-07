package com.ufcg.psoft.commerce.service.imposto;

import com.ufcg.psoft.commerce.enums.AtivoTipo;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ImpostoAcaoStrategy implements ImpostoStategy {
  private static final BigDecimal ALIQUOTA = new BigDecimal("0.15");

  @Override
  public boolean supports(AtivoTipo tipo) {
    return AtivoTipo.ACAO.equals(tipo);
  }

  @Override
  public BigDecimal calcular(BigDecimal lucro) {
    if (lucro.signum() <= 0) {
      return BigDecimal.ZERO;
    }
    return lucro.multiply(ALIQUOTA);
  }
}

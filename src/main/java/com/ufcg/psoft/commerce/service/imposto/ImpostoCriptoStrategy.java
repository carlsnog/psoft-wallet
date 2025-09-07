package com.ufcg.psoft.commerce.service.imposto;

import com.ufcg.psoft.commerce.enums.AtivoTipo;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

@Component
public class ImpostoCriptoStrategy implements ImpostoStategy {
  private static final BigDecimal TETO_FAIXA = new BigDecimal("5000");
  private static final BigDecimal ALIQUOTA_ATE_TETO = new BigDecimal("0.15");
  private static final BigDecimal ALIQUOTA_ACIMA_TETO = new BigDecimal("0.25");

  @Override
  public boolean supports(AtivoTipo tipo) {
    return AtivoTipo.CRIPTO.equals(tipo);
  }

  @Override
  public BigDecimal calcular(BigDecimal lucro) {
    if (lucro.signum() <= 0) {
      return BigDecimal.ZERO;
    }
    var aliquota = lucro.compareTo(TETO_FAIXA) <= 0 ? ALIQUOTA_ATE_TETO : ALIQUOTA_ACIMA_TETO;
    return lucro.multiply(aliquota);
  }
}

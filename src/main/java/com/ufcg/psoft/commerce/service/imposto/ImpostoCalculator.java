package com.ufcg.psoft.commerce.service.imposto;

import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ImpostoCalculator {
  private final List<ImpostoStategy> strategies;

  public ImpostoCalculator(List<ImpostoStategy> strategies) {
    this.strategies = strategies;
  }

  public BigDecimal calcular(AtivoTipo tipo, BigDecimal lucro) {
    return strategies.stream()
        .filter(s -> s.supports(tipo))
        .findFirst()
        .orElseThrow(() -> new CommerceException(ErrorCode.TIPO_ATIVO_INVALIDO))
        .calcular(lucro);
  }
}

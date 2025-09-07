package com.ufcg.psoft.commerce.service.imposto;

import com.ufcg.psoft.commerce.enums.AtivoTipo;
import java.math.BigDecimal;

public interface ImpostoStategy {

  boolean supports(AtivoTipo tipo);

  BigDecimal calcular(BigDecimal lucro);
}

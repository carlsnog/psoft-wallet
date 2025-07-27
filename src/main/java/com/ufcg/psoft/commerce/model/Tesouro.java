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
  public void atualizarValor(BigDecimal novoValor) {
    throw new CommerceException(ErrorCode.OPERACAO_INVALIDA_PARA_O_TIPO);
  }
}

package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class Tesouro extends Ativo {

  @Builder.Default private final AtivoTipo tipo = AtivoTipo.TESOURO;

  @Override
  public void atualizarValor(BigDecimal novoValor){
    throw new CommerceException(ErrorCode.OPERACAO_INVALIDA_PARA_O_TIPO);
  }
}

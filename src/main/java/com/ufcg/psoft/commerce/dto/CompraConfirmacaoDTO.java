package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.CompraStatusEnum;
import lombok.Data;

@Data
public class CompraConfirmacaoDTO {

  @JsonProperty("status")
  private CompraStatusEnum
      statusAtual; // Usado como hash para evitar que requests duplicadas ou a partir de dados stale
  // causem processamento duplicado
}

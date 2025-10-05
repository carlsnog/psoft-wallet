package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.transacao.compra.CompraStatusEnum;
import lombok.Data;

@Data
public class CompraConfirmacaoDTO {

  @JsonProperty("status_atual")
  private CompraStatusEnum
      statusAtual; // Usado como hash para evitar que requests duplicadas ou a partir de dados stale
  // causem processamento duplicado
}

package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompraCreateDTO {

  @JsonProperty("ativoId")
  @NotNull(message = "ID do ativo é obrigatório")
  private Long ativoId;

  @JsonProperty("quantidade")
  @NotNull(message = "Quantidade é obrigatória")
  @Positive(message = "Quantidade deve ser positiva")
  private int quantidade;
}

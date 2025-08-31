package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResgateCreateDTO {

  @JsonProperty("ativoId")
  @NotNull(message = "ID do ativo é obrigatório")
  private Long ativoId;

  @JsonProperty("quantidade")
  @NotNull(message = "Quantidade é obrigatória")
  @Positive(message = "Quantidade deve ser positiva")
  private int quantidade;
}

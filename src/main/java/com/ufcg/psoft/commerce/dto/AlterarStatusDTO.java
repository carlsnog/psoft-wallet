package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlterarStatusDTO {

  @JsonProperty("novoStatus")
  @NotNull(message = "Novo status é obrigatório")
  private StatusAtivo novoStatus;
}

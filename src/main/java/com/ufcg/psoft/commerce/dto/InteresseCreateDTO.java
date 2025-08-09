package com.ufcg.psoft.commerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteresseCreateDTO {

  @NotNull(message = "ID do cliente e obrigatorio")
  private Long clienteId;

  @NotNull(message = "ID do ativo e obrigatorio")
  private Long ativoId;
}

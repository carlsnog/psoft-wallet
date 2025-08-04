package com.ufcg.psoft.commerce.dto;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
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

  @NotNull(message = "Tipo é obrigatório")
  private TipoInteresseEnum tipo;

  @NotNull(message = "ID do cliente é obrigatório")
  private Long clienteId;

  @NotNull(message = "ID do ativo é obrigatório")
  private Long ativoId;
}

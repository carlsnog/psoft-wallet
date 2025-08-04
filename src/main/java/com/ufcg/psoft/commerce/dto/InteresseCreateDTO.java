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

  @NotNull(message = "Tipo e obrigatorio")
  private TipoInteresseEnum tipo;

  @NotNull(message = "ID do cliente e obrigatorio")
  private Long clienteId;

  @NotNull(message = "ID do ativo e obrigatorio")
  private Long ativoId;
}

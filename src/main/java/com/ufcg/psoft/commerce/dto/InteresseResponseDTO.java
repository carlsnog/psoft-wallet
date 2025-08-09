package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.model.Interesse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteresseResponseDTO {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("tipo")
  @NotNull(message = "Tipo de interesse obrigatório")
  private TipoInteresseEnum tipo;

  @JsonProperty("clienteId")
  @NotNull(message = "Id do cliente obrigatório")
  private Long clienteId;

  @JsonProperty("ativoId")
  @NotNull(message = "Id do ativo obrigatório")
  private Long ativoId;

  public InteresseResponseDTO(Interesse interesse) {
    this.id = interesse.getId();
    this.tipo = interesse.getTipo();
    this.clienteId = interesse.getClienteId();
    this.ativoId = interesse.getAtivoId();
  }
}

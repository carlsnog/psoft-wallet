package com.ufcg.psoft.commerce.dto;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.model.Interesse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InteresseResponseDTO {

  private Long id;
  private TipoInteresseEnum tipo;
  private Long clienteId;
  private Long ativoId;

  public InteresseResponseDTO(Interesse interesse) {
    this.id = interesse.getId();
    this.tipo = interesse.getTipo();
    this.clienteId = interesse.getClienteId();
    this.ativoId = interesse.getAtivoId();
  }
}

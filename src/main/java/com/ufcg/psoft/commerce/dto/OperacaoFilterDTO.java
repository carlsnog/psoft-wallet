package com.ufcg.psoft.commerce.dto;

import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.enums.TipoTransacao;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperacaoFilterDTO {
  private AtivoTipo ativoTipo;
  private Long ativoId;
  private Long clienteId;
  private TipoTransacao tipoTransacao;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
}

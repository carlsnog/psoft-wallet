package com.ufcg.psoft.commerce.dto;

import com.ufcg.psoft.commerce.enums.TipoTransacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperacaoResponseDTO {
  private Long id;
  private TipoTransacao tipoTransacao;
  private Long clienteId;
  private Long ativoId;
  private String ativoNome;
  private BigDecimal valorTotal;
  private String status;
  private LocalDateTime inicio;
  private LocalDateTime fim;
}

package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.transacao.compra.CompraStatusEnum;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompraFilterDTO {

  @JsonProperty("clienteId")
  @NotNull(message = "ID do cliente é obrigatório")
  private Long clienteId;

  private CompraStatusEnum status;
  private Long ativoId;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
}

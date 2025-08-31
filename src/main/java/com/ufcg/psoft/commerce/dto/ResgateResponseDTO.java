package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.ResgateStatusEnum;
import com.ufcg.psoft.commerce.model.resgate.Resgate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResgateResponseDTO {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("ativoId")
  private Long ativoId;

  @JsonProperty("quantidade")
  private int quantidade;

  @JsonProperty("valorUnitario")
  private BigDecimal valorUnitario;

  @JsonProperty("valorTotal")
  private BigDecimal valorTotal;

  @JsonProperty("solicitadoEm")
  private LocalDateTime solicitadoEm;

  @JsonProperty("finalizadoEm")
  private LocalDateTime finalizadoEm;

  @JsonProperty("status")
  private ResgateStatusEnum status;

  public ResgateResponseDTO(Resgate resgate) {
    this.id = resgate.getId();
    this.ativoId = resgate.getAtivoId();
    this.quantidade = resgate.getQuantidade();
    this.valorUnitario = resgate.getValorUnitario();
    this.valorTotal = resgate.getValorTotal();
    this.solicitadoEm = resgate.getSolicitadoEm();
    this.finalizadoEm = resgate.getFinalizadoEm();
    this.status = resgate.getStatus();
  }
}

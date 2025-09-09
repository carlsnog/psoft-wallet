package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.compra.CompraStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompraResponseDTO {

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

  @JsonProperty("abertaEm")
  private LocalDateTime abertaEm;

  @JsonProperty("finalizadaEm")
  private LocalDateTime finalizadaEm;

  @JsonProperty("status")
  private CompraStatusEnum status;

  public CompraResponseDTO(Compra compra) {
    this.id = compra.getId();
    this.ativoId = compra.getAtivoId();
    this.quantidade = compra.getQuantidade();
    this.valorUnitario = compra.getValorUnitario();
    this.valorTotal = compra.getValorTotal();
    this.abertaEm = compra.getAbertaEm();
    this.finalizadaEm = compra.getFinalizadaEm();
    this.status = compra.getStatus();
  }
}

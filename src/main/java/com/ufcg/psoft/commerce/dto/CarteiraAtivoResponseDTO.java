package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.model.AtivoCarteira;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarteiraAtivoResponseDTO {

  @JsonProperty("ativo_id")
  private Long ativoId;

  @JsonProperty("ativo_nome")
  private String ativoNome;

  @JsonProperty("ativo_tipo")
  private AtivoTipo ativoTipo;

  @JsonProperty("lucro")
  private BigDecimal lucro;

  @JsonProperty("valor")
  private BigDecimal valorUnitario;

  @JsonProperty("valor_total")
  private BigDecimal valorTotal;

  @JsonProperty("quantidade")
  private int quantidade;

  public CarteiraAtivoResponseDTO(AtivoCarteira ativoCarteira) {
    this.ativoId = ativoCarteira.getAtivo().getId();
    this.ativoNome = ativoCarteira.getAtivo().getNome();
    this.ativoTipo = ativoCarteira.getAtivo().getTipo();
    this.lucro = ativoCarteira.getLucro();
    this.valorUnitario = ativoCarteira.getValor();
    this.valorTotal = ativoCarteira.getValor();
    this.quantidade = 1;
  }

  public void adicionar(AtivoCarteira ativoCarteira) {
    this.quantidade += 1;
    this.valorTotal = this.valorTotal.add(ativoCarteira.getValor());
    this.lucro = this.lucro.add(ativoCarteira.getLucro());
  }
}

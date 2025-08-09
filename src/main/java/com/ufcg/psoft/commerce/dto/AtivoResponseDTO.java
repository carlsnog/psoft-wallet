package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.model.Ativo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AtivoResponseDTO {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("nome")
  @NotBlank(message = "Nome do ativo obrigatorio")
  private String nome;

  @JsonProperty("descricao")
  @NotBlank(message = "Descricao do ativo obrigatoria")
  private String descricao;

  @JsonProperty("status")
  @NotBlank(message = "Status de disponibilidade obrigatorio")
  private StatusAtivo status;

  @JsonProperty("cotacao")
  @NotNull(message = "Cotação do ativo é obrigatória")
  @Positive(message = "Cotação deve ser positiva")
  private BigDecimal cotacao;

  @JsonProperty("tipo")
  private AtivoTipo tipo;

  public AtivoResponseDTO(Ativo ativo) {
    this.id = ativo.getId();
    this.nome = ativo.getNome();
    this.descricao = ativo.getDescricao();
    this.cotacao = ativo.getCotacao();
    this.status = ativo.getStatus();
    this.tipo = ativo.getTipo();
  }
}

package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AtivoUpsertDTO {

  @JsonProperty("nome")
  @NotBlank(message = "Nome do ativo obrigatorio")
  private String nome;

  @JsonProperty("descricao")
  @NotBlank(message = "Descricao do ativo obrigatoria")
  private String descricao;

  @JsonProperty("status")
  @NotNull(message = "Status é obrigatório")
  private StatusAtivo status;

  @JsonProperty("valor")
  @Digits(
      integer = 15,
      fraction = 2,
      message = "Valor deve ter no máximo 15 dígitos inteiros e 2 decimais")
  @DecimalMin(
      value = "0.01",
      inclusive = true,
      message = "Valor deve ser positivo e maior que zero")
  @NotNull(message = "Valor é obrigatório")
  private BigDecimal valor;

  @JsonProperty("tipo")
  @NotNull(message = "Tipo é obrigatório")
  private AtivoTipo tipo;
}

package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AtivoUpdateDTO {

  @JsonProperty("nome")
  @NotBlank(message = "Nome do ativo obrigatorio")
  private String nome;

  @JsonProperty("descricao")
  @NotBlank(message = "Descricao do ativo obrigatoria")
  private String descricao;
}

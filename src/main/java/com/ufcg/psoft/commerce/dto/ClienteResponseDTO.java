package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.model.Cliente;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {

  @JsonProperty("id")
  private Long id;

  @JsonProperty("nome")
  @NotBlank(message = "Nome obrigatorio")
  private String nome;

  @JsonProperty("endereco")
  @NotBlank(message = "Endereco obrigatorio")
  private String endereco;

  @JsonProperty("plano")
  @Enumerated(EnumType.STRING)
  private PlanoEnum plano;

  public ClienteResponseDTO(Cliente cliente) {
    this.id = cliente.getId();
    this.nome = cliente.getNome();
    this.endereco = cliente.getEndereco();
    this.plano = cliente.getPlano();
  }
}


package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtivoUpsertDTO {

    @JsonProperty("nome")
    @NotBlank(message = "Nome do ativo obrigatorio")
    private String nome;

    @JsonProperty("descricao")
    @NotBlank(message = "Descricao do ativo obrigatoria")
    private String descricao;

    @JsonProperty("Status de disponibilidade")
    private StatusAtivo status;

    @JsonProperty("valor")
    private BigDecimal valor;

    @JsonProperty("tipo")
    private AtivoTipo tipo;

}

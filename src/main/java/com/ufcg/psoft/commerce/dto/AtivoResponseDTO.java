package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.model.Ativo;
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
public class AtivoResponseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    @NotBlank(message = "Nome do ativo obrigatorio")
    private String nome;

    @JsonProperty("descricao")
    @NotBlank(message = "Descricao do ativo obrigatoria")
    private String descricao;

    @JsonProperty("Status de disponibilidade")
    @NotBlank(message = "Status de disponibilidade obrigatorio")
    private StatusAtivo status;

    @JsonProperty("valor")
    @NotBlank(message = "Valor do ativo obrigatorio")
    private BigDecimal valor;

    @JsonProperty("tipo")
    private AtivoTipo tipo;


    public AtivoResponseDTO(Ativo ativo){
        this.id = ativo.getId();
        this.nome = ativo.getNome();
        this.descricao = ativo.getDescricao();
        this.valor = ativo.getValor();
        this.tipo = ativo.getTipo();
    }
}

package com.ufcg.psoft.commerce.http.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodigoAcessoDto {

    @NotBlank
    @JsonProperty("codigoAcesso")
    private String codigoAcesso;

}

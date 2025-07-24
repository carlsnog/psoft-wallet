package com.ufcg.psoft.commerce.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Endereco {
    
    @NotBlank(message = "Endereco obrigatorio")
    @Column(nullable = false)
    private String rua;

    @NotBlank
    @Column(nullable = false)
    private String numero;

    @NotBlank
    @Column(nullable = false)
    private String cep;

}

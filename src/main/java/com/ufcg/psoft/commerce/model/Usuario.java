package com.ufcg.psoft.commerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
@Table(name = "usuarios")
public abstract class Usuario {

    @Id
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false, name = "codigo_acesso")
    @NotBlank(message = "Codigo de acesso obrigatorio")
    @JsonIgnore
    private String codigoAcesso;

    @Column(nullable = false)
    private String nome;

    @Embedded
    @Column(nullable = false)
    @JsonIgnore
    private Endereco endereco;


}

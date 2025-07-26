package com.ufcg.psoft.commerce.model;

import jakarta.persistence.*;
import lombok.*;
import com.ufcg.psoft.commerce.enums.*;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Ativo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAtivo status;

    @Column(nullable = false, scale = 2, precision = 19)
    private BigDecimal valor;

    private AtivoTipo tipo;
}

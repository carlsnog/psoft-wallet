package com.ufcg.psoft.commerce.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity
@Table(name = "cliente")
public class Cliente extends Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String codigoAcesso;

    @Getter
    @Column(nullable = false)
    private String nome;

    @Getter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PlanoEnum plano;

    @Getter
    @Column(nullable = false)
    private String endereco;

    @Override
    public boolean isAdmin() {
        return false;
    }

}

package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.auth.Usuario;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "cliente")
public class Cliente extends Usuario {

    public Cliente(String codigoAcesso, String nome, PlanoEnum plano, Endereco endereco) {
        super(codigoAcesso);
        this.plano = plano;
        this.endereco = endereco;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    private PlanoEnum plano;

    @Embedded
    private Endereco endereco;

    @Override
    public boolean isAdmin() {
        return false;
    }

}

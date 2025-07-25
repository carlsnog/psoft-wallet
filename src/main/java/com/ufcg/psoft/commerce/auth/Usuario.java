package com.ufcg.psoft.commerce.auth;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class Usuario {

    @Column(nullable = false)
    private final String codigoAcesso;

    public Usuario(String codigoAcesso) {
        this.codigoAcesso = codigoAcesso;
    }

    public abstract boolean isAdmin();

    public boolean validate(String codigoAcesso) {
        return this.codigoAcesso.equals(codigoAcesso); // Aqui podia calcular o hash do código para não salvar o código
                                                       // em plain text
    }
}

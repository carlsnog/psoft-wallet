package com.ufcg.psoft.commerce.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class Usuario {

    public abstract boolean isAdmin();

    public boolean validar(String codigoAcesso) {
        return this.getCodigoAcesso().equals(codigoAcesso); // Aqui podia calcular o hash do código para não salvar em
                                                            // plain text
    }

    public abstract String getCodigoAcesso();

    public abstract long getId();
}

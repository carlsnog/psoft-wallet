package com.ufcg.psoft.commerce.model;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class Usuario {

  public abstract boolean isAdmin();

  public boolean validar(String codigoAcesso) {
    return this.getCodigoAcesso().equals(codigoAcesso);
  }

  public abstract String getCodigoAcesso();

  public abstract long getId();
}

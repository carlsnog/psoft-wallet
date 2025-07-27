package com.ufcg.psoft.commerce.model;

public interface Usuario {

  public abstract boolean isAdmin();

  public default boolean validar(String codigoAcesso) {
    return this.getCodigoAcesso().equals(codigoAcesso);
  }

  public abstract String getCodigoAcesso();

  public abstract long getId();
}

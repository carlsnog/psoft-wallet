package com.ufcg.psoft.commerce.model;

public interface Usuario {

  public default boolean validar(String userId, String codigoAcesso) {
    return this.getUserId().equals(userId) && this.getCodigoAcesso().equals(codigoAcesso);
  }

  public abstract String getUserId();

  public abstract String getCodigoAcesso();

  public abstract boolean isAdmin();
}

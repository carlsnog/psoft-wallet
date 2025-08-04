package com.ufcg.psoft.commerce.model;

public abstract class Usuario {

  public boolean validar(String userId, String codigoAcesso) {
    return this.getUserId().equals(userId) && this.getCodigoAcesso().equals(codigoAcesso);
  }

  public abstract String getUserId();

  public abstract String getCodigoAcesso();

  public abstract boolean isAdmin();
}

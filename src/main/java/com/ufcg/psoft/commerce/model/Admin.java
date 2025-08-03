package com.ufcg.psoft.commerce.model;

import lombok.Getter;

@Getter
public class Admin implements Usuario {

  private static Admin instance;

  private static final String COD_ADMIN_PADRAO = "admin@123";
  private static final String COD_ADMIN_ENV = "CODIGO_ACESSO_ADMIN";

  private long id;
  private String codigoAcesso;

  private Admin(long id, String codigoAcesso) {
    this.id = id;
    this.codigoAcesso = codigoAcesso;
  }

  @Override
  public boolean isAdmin() {
    return true;
  }

  public static Admin getInstance() {
    String codigoAcesso = System.getenv(COD_ADMIN_ENV);
    if (codigoAcesso == null) {
      codigoAcesso = COD_ADMIN_PADRAO;
    }

    if (instance == null) {
      instance = new Admin(0, codigoAcesso);
    }

    return instance;
  }
}

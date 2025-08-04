package com.ufcg.psoft.commerce.model;

import lombok.Getter;

@Getter
public class Admin implements Usuario {

  private static Admin instance;

  private static final String ID_ADMIN_PADRAO = "admin";
  private static final String COD_ADMIN_PADRAO = "admin@123";

  private String userId;
  private String codigoAcesso;

  private Admin(String id, String codigoAcesso) {
    this.userId = id;
    this.codigoAcesso = codigoAcesso;
  }

  @Override
  public boolean isAdmin() {
    return true;
  }

  public static Admin getInstance() {
    if (instance == null) {
      instance = new Admin(ID_ADMIN_PADRAO, COD_ADMIN_PADRAO);
    }

    return instance;
  }
}

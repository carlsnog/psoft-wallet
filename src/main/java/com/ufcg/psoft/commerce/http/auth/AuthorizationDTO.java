package com.ufcg.psoft.commerce.http.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AuthorizationDTO {
  private String userId;
  private String codigoAcesso;

  public AuthorizationDTO() {
    this.userId = "";
    this.codigoAcesso = "";
  }

  public static AuthorizationDTO from(String authorization) throws IllegalArgumentException {
    var parts = authorization.split(":");

    String userId;
    String codigoAcesso;
    if (parts.length != 2) {
      throw new IllegalArgumentException("Formato inválido");
    }

    userId = parts[0];
    codigoAcesso = parts[1];

    return AuthorizationDTO.builder().userId(userId).codigoAcesso(codigoAcesso).build();
  }
}

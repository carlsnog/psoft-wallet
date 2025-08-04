package com.ufcg.psoft.commerce.service.auth.autenticador;

import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import java.util.Optional;

public abstract class Autenticador {

  protected final ClienteRepository clienteRepository;

  public Autenticador(ClienteRepository clienteRepository) {
    this.clienteRepository = clienteRepository;
  }

  public abstract Optional<Usuario> autenticar(String id, String codigoAcesso)
      throws CommerceException;

  protected boolean validateAdmin(String id, String codigoAcesso) {
    var admin = Admin.getInstance();

    if (admin.validar(id, codigoAcesso)) {
      return true;
    }

    return false;
  }

  protected Cliente getClientePorId(String userId) {
    long id;
    try {
      id = Long.parseLong(userId);
    } catch (NumberFormatException e) {
      throw new CommerceException(ErrorCode.UNAUTHORIZED);
    }

    return clienteRepository
        .findById(id)
        .orElseThrow(() -> new CommerceException(ErrorCode.UNAUTHORIZED));
  }
}

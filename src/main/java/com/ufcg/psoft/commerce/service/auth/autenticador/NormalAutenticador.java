package com.ufcg.psoft.commerce.service.auth.autenticador;

import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import java.util.Optional;

public class NormalAutenticador extends Autenticador {

  public NormalAutenticador(ClienteRepository clienteRepository) {
    super(clienteRepository);
  }

  @Override
  public Optional<Usuario> autenticar(long id, String codigoAcesso) {
    if (validateAdmin(id, codigoAcesso)) {
      return Optional.of(Admin.getInstance());
    }

    var usuario = clienteRepository.findById(id);
    if (usuario.isEmpty()) {
      throw new CommerceException(ErrorCode.UNAUTHORIZED);
    }

    if (!usuario.get().validar(codigoAcesso)) {
      throw new CommerceException(ErrorCode.UNAUTHORIZED);
    }

    return Optional.of(usuario.get());
  }
}

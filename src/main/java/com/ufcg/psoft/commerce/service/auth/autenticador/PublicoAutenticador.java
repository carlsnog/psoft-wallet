package com.ufcg.psoft.commerce.service.auth.autenticador;

import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import java.util.Optional;

public class PublicoAutenticador extends Autenticador {

  public PublicoAutenticador(ClienteRepository clienteRepository) {
    super(clienteRepository);
  }

  @Override
  public Optional<Usuario> autenticar(String userId, String codigoAcesso) {
    return Optional.empty();
  }
}

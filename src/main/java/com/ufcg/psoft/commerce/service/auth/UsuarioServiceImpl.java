package com.ufcg.psoft.commerce.service.auth;

import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.service.auth.autenticador.AutenticadorFactory;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

  private final AutenticadorFactory autenticadorFactory;

  public UsuarioServiceImpl(AutenticadorFactory autenticadorFactory) {
    this.autenticadorFactory = autenticadorFactory;
  }

  @Override
  public Optional<Usuario> getUsuario(
      String userId, String codigoAcesso, TipoAutenticacao tipoAutenticacao) {
    var autenticador = autenticadorFactory.getAutenticador(tipoAutenticacao);
    var usuario = autenticador.autenticar(userId, codigoAcesso);

    return usuario;
  }
}

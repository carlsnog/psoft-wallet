package com.ufcg.psoft.commerce.service.auth;

import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.model.Usuario;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public interface UsuarioService {
  public Optional<Usuario> getUsuario(
      long id, String codigoAcesso, TipoAutenticacao tipoAutenticacao);
}

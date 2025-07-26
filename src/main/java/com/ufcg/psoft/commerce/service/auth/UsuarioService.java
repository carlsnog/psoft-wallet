package com.ufcg.psoft.commerce.service.auth;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ufcg.psoft.commerce.model.Usuario;

@Service
public interface UsuarioService {
    public Optional<Usuario> getUsuario(long id, String codigoAcesso, TipoAutenticacao tipoAutenticacao);

    public boolean isCodigoValido(String codigoAcesso);
}

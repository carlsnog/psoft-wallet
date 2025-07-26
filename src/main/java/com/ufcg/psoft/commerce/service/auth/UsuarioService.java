package com.ufcg.psoft.commerce.service.auth;

import org.springframework.stereotype.Service;

import com.ufcg.psoft.commerce.model.Usuario;

@Service
public interface UsuarioService {
    public Usuario getUsuario(long id, String codigoAcesso, TipoAutenticacao tipoAutenticacao);
    public boolean isCodigoValido(String codigoAcesso);
}

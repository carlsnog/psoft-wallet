package com.ufcg.psoft.commerce.service.auth;

import org.springframework.stereotype.Service;

import com.ufcg.psoft.commerce.service.auth.autenticador.AutenticadorFactory;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Usuario;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final AutenticadorFactory autenticadorFactory;

    public UsuarioServiceImpl(AutenticadorFactory autenticadorFactory) {
        this.autenticadorFactory = autenticadorFactory;
    }

    public Usuario getUsuario(long id, String codigoAcesso, TipoAutenticacao tipoAutenticacao) {
        var autenticador = autenticadorFactory.getAutenticador(tipoAutenticacao);
        var usuario = autenticador.autenticar(id, codigoAcesso);

        if (usuario.isEmpty()) {
            throw new CommerceException(ErrorCode.UNAUTHORIZED);
        }

        return usuario.get();
    }

    public boolean isCodigoValido(String codigoAcesso) {
        return codigoAcesso.matches("\\d{6}");
    }
}

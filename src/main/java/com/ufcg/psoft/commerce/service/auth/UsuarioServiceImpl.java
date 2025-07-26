package com.ufcg.psoft.commerce.service.auth;

import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.ufcg.psoft.commerce.service.auth.autenticador.AutenticadorFactory;
import com.ufcg.psoft.commerce.model.Usuario;

@Service
public class UsuarioServiceImpl implements UsuarioService {
    private final AutenticadorFactory autenticadorFactory;

    public UsuarioServiceImpl(AutenticadorFactory autenticadorFactory) {
        this.autenticadorFactory = autenticadorFactory;
    }

    public Optional<Usuario> getUsuario(long id, String codigoAcesso, TipoAutenticacao tipoAutenticacao) {
        var autenticador = autenticadorFactory.getAutenticador(tipoAutenticacao);
        var usuario = autenticador.autenticar(id, codigoAcesso);

        return usuario;
    }

    public boolean isCodigoValido(String codigoAcesso) {
        return codigoAcesso.matches("\\d{6}");
    }
}

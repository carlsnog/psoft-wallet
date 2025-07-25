package com.ufcg.psoft.commerce.auth.autenticador;

import java.util.Optional;

import com.ufcg.psoft.commerce.auth.Admin;
import com.ufcg.psoft.commerce.auth.Usuario;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.repository.ClienteRepository;

public class NormalAutenticador extends Autenticador {
    public NormalAutenticador(ClienteRepository clienteRepository) {
        super(clienteRepository);
    }

    @Override
    public Optional<Usuario> autenticar(String codigoAcesso) {
        if (validateAdmin(codigoAcesso)) {
            return Optional.of(Admin.getInstance());
        }

        var usuario = clienteRepository.findByCodigoAcesso(codigoAcesso);
        if (usuario.isEmpty()) {
            throw new CommerceException(ErrorCode.UNAUTHORIZED);
        }

        return Optional.of(usuario.get());
    }
}

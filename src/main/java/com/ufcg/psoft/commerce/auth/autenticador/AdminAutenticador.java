package com.ufcg.psoft.commerce.auth.autenticador;

import com.ufcg.psoft.commerce.auth.Admin;
import com.ufcg.psoft.commerce.auth.Usuario;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.repository.ClienteRepository;

import java.util.Optional;

public class AdminAutenticador extends Autenticador {
    public AdminAutenticador(ClienteRepository clienteRepository) {
        super(clienteRepository);
    }

    @Override
    public Optional<Usuario> autenticar(String codigoAcesso) {
        if (validateAdmin(codigoAcesso)) {
            return Optional.of(Admin.getInstance());
        }

        throw new CommerceException(ErrorCode.UNAUTHORIZED);
    }
}

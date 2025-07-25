package com.ufcg.psoft.commerce.auth.autenticador;

import com.ufcg.psoft.commerce.auth.Admin;
import com.ufcg.psoft.commerce.auth.Usuario;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.repository.ClienteRepository;

import java.util.Optional;

public abstract class Autenticador {
    protected final ClienteRepository clienteRepository;

    public Autenticador(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public abstract Optional<Usuario> autenticar(String codigoAcesso) throws CommerceException;

    protected boolean validateAdmin(String codigoAcesso) {
        var admin = Admin.getInstance();
        return admin.validate(codigoAcesso);
    }
}

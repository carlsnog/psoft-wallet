package com.ufcg.psoft.commerce.service.auth.autenticador;

import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.ClienteRepository;

import java.util.Optional;

public abstract class Autenticador {
    protected final ClienteRepository clienteRepository;

    public Autenticador(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public abstract Optional<Usuario> autenticar(long id, String codigoAcesso) throws CommerceException;

    protected boolean validateAdmin(long id, String codigoAcesso) {
        if (id != 0) {
            return false;
        }

        var admin = Admin.getInstance();
        return admin.validar(codigoAcesso);
    }
}

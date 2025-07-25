package com.ufcg.psoft.commerce.auth.autenticador;

import java.util.Optional;

import com.ufcg.psoft.commerce.auth.Usuario;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.PlanoEnum;
import com.ufcg.psoft.commerce.auth.Admin;
import com.ufcg.psoft.commerce.repository.ClienteRepository;

public class PremiumAutenticador extends Autenticador {
    public PremiumAutenticador(ClienteRepository clienteRepository) {
        super(clienteRepository);
    }

    @Override
    public Optional<Usuario> autenticar(String codigoAcesso) {
        if (validateAdmin(codigoAcesso)) {
            return Optional.of(Admin.getInstance());
        }

        Optional<Cliente> clienteRes = clienteRepository.findByCodigoAcesso(codigoAcesso);
        if (clienteRes == null) {
            throw new CommerceException(ErrorCode.UNAUTHORIZED);
        }

        var cliente = clienteRes.get();
        if (cliente.getPlano() != PlanoEnum.PREMIUM) {
            throw new CommerceException(ErrorCode.FORBIDDEN);
        }

        return Optional.of(cliente);
    }
}

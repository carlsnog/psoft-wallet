
package com.ufcg.psoft.commerce.service.auth.autenticador;

import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.PlanoEnum;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import java.util.Optional;

public class PremiumAutenticador extends Autenticador {
    public PremiumAutenticador(ClienteRepository clienteRepository) {
        super(clienteRepository);
    }

    @Override
    public Optional<Usuario> autenticar(long id, String codigoAcesso) {
        if (validateAdmin(id, codigoAcesso)) {
            return Optional.of(Admin.getInstance());
        }

        Optional<Cliente> clienteRes = clienteRepository.findByCodigoAcesso(codigoAcesso);
        if (clienteRes == null) {
            throw new CommerceException(ErrorCode.UNAUTHORIZED);
        }

        var cliente = clienteRes.get();

        if (!cliente.validar(codigoAcesso)) {
            throw new CommerceException(ErrorCode.UNAUTHORIZED);
        }

        if (cliente.getPlano() != PlanoEnum.PREMIUM) {
            throw new CommerceException(ErrorCode.FORBIDDEN);
        }

        return Optional.of(cliente);
    }
}

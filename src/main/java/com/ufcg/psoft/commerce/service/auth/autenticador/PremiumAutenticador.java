package com.ufcg.psoft.commerce.service.auth.autenticador;

import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import java.util.Optional;

public class PremiumAutenticador extends Autenticador {

  public PremiumAutenticador(ClienteRepository clienteRepository) {
    super(clienteRepository);
  }

  @Override
  public Optional<Usuario> autenticar(String userId, String codigoAcesso) {
    if (validateAdmin(userId, codigoAcesso)) {
      return Optional.of(Admin.getInstance());
    }

    var cliente = getClientePorId(userId);

    if (!cliente.validar(userId, codigoAcesso)) {
      throw new CommerceException(ErrorCode.UNAUTHORIZED);
    }

    if (cliente.getPlano() != PlanoEnum.PREMIUM) {
      throw new CommerceException(ErrorCode.FORBIDDEN);
    }

    return Optional.of(cliente);
  }
}

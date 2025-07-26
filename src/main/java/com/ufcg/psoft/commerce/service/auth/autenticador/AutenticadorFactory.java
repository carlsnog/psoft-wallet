package com.ufcg.psoft.commerce.service.auth.autenticador;

import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class AutenticadorFactory {

  private static final Map<TipoAutenticacao, Function<ClienteRepository, Autenticador>>
      AUTENTICADORES_MAP =
          Map.of(
              TipoAutenticacao.ADMIN, AdminAutenticador::new,
              TipoAutenticacao.NORMAL, NormalAutenticador::new,
              TipoAutenticacao.PREMIUM, PremiumAutenticador::new,
              TipoAutenticacao.PUBLICA, PublicoAutenticador::new);

  private final ClienteRepository clienteRepository;

  public AutenticadorFactory(ClienteRepository clienteRepository) {
    this.clienteRepository = clienteRepository;
  }

  public Autenticador getAutenticador(TipoAutenticacao tipoAutenticacao) {
    var construtor = AUTENTICADORES_MAP.get(tipoAutenticacao);
    if (construtor == null) {
      throw new IllegalArgumentException("Tipo de autenticação inválido");
    }

    return construtor.apply(this.clienteRepository);
  }
}

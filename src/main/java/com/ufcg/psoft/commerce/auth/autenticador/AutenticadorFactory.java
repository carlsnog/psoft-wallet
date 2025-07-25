package com.ufcg.psoft.commerce.auth.autenticador;

import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.ufcg.psoft.commerce.auth.TipoAutenticacao;
import com.ufcg.psoft.commerce.repository.ClienteRepository;

@Component
public class AutenticadorFactory {
    private static final Map<TipoAutenticacao, Function<ClienteRepository, Autenticador>> autenticadoresMap = Map.of(
            TipoAutenticacao.ADMIN, AdminAutenticador::new,
            TipoAutenticacao.NORMAL, NormalAutenticador::new,
            TipoAutenticacao.PREMIUM, PremiumAutenticador::new,
            TipoAutenticacao.PUBLICA, PublicoAutenticador::new);

    private final ClienteRepository clienteRepository;

    public AutenticadorFactory(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Autenticador getAutenticador(TipoAutenticacao tipoAutenticacao) {
        var construtor = autenticadoresMap.get(tipoAutenticacao);
        if (construtor == null) {
            throw new IllegalArgumentException("Tipo de autenticação inválido");
        }

        return construtor.apply(this.clienteRepository);
    }
}

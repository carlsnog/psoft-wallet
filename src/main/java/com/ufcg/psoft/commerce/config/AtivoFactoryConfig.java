package com.ufcg.psoft.commerce.config;

import com.ufcg.psoft.commerce.dto.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;

@Configuration
public class AtivoFactoryConfig {

    @Bean
    public Map<String, Function<AtivoPostPutRequestDTO, Ativo>> ativoBuilders() {
        return Map.of(
                "CRIPTO", dto -> Cripto.builder()
                        .nome(dto.getNome())
                        .descricao(dto.getDescricao())
                        .status(dto.getStatus())
                        .valor(dto.getValor())
                        .build(),
                "TESOURO", dto -> Tesouro.builder()
                        .nome(dto.getNome())
                        .descricao(dto.getDescricao())
                        .status(dto.getStatus())
                        .valor(dto.getValor())
                        .build(),
                "ACAO", dto -> Acao.builder()
                        .nome(dto.getNome())
                        .descricao(dto.getDescricao())
                        .status(dto.getStatus())
                        .valor(dto.getValor())
                        .build()
        );
    }
}

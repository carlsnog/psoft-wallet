package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoUpsertDTO;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.*;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class AtivoFactory {

    private static final Map<AtivoTipo, Function<AtivoUpsertDTO, Ativo>> builders = Map.of(
            AtivoTipo.CRIPTO, dto -> Cripto.builder()
                    .nome(dto.getNome())
                    .descricao(dto.getDescricao())
                    .status(dto.getStatus())
                    .valor(dto.getValor())
                    .build(),
            AtivoTipo.TESOURO, dto -> Tesouro.builder()
                    .nome(dto.getNome())
                    .descricao(dto.getDescricao())
                    .status(dto.getStatus())
                    .valor(dto.getValor())
                    .build(),
            AtivoTipo.ACAO, dto -> Acao.builder()
                    .nome(dto.getNome())
                    .descricao(dto.getDescricao())
                    .status(dto.getStatus())
                    .valor(dto.getValor())
                    .build()
    );

    public Ativo criarAtivo(AtivoUpsertDTO dto) {
        Function<AtivoUpsertDTO, Ativo> builder = builders.get(dto.getTipo());
        if (builder == null) {
            throw new CommerceException(ErrorCode.TIPO_ATIVO_INVALIDO);
        }
        return builder.apply(dto);
    }


}

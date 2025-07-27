package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoUpsertDTO;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.*;
import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class AtivoFactory {

  private static final Map<AtivoTipo, Function<AtivoUpsertDTO, Ativo>> ATIVOS_BUILDER_MAP =
      Map.of(
          AtivoTipo.CRIPTO,
          dto ->
              Cripto.builder()
                  .nome(dto.getNome())
                  .descricao(dto.getDescricao())
                  .status(dto.getStatus())
                  .valor(dto.getValor())
                  .tipo(AtivoTipo.CRIPTO)
                  .build(),
          AtivoTipo.TESOURO,
          dto ->
              Tesouro.builder()
                  .nome(dto.getNome())
                  .descricao(dto.getDescricao())
                  .status(dto.getStatus())
                  .valor(dto.getValor())
                  .tipo(AtivoTipo.TESOURO)
                  .build(),
          AtivoTipo.ACAO,
          dto ->
              Acao.builder()
                  .nome(dto.getNome())
                  .descricao(dto.getDescricao())
                  .status(dto.getStatus())
                  .valor(dto.getValor())
                  .tipo(AtivoTipo.ACAO)
                  .build());

  public Ativo criarAtivo(AtivoUpsertDTO dto) {
    Function<AtivoUpsertDTO, Ativo> builder = ATIVOS_BUILDER_MAP.get(dto.getTipo());
    if (builder == null) {
      throw new CommerceException(ErrorCode.TIPO_ATIVO_INVALIDO);
    }
    return builder.apply(dto);
  }
}

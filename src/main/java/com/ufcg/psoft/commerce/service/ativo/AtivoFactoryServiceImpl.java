package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.Ativo;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;

@Service
public class AtivoFactoryServiceImpl implements AtivoFactoryService{

    private final Map<String, Function<AtivoPostPutRequestDTO, Ativo>> builders;

    public AtivoFactoryServiceImpl(Map<String, Function<AtivoPostPutRequestDTO, Ativo>> builders) {
        this.builders = builders;
    }

    @Override
    public Ativo criarAtivo(AtivoPostPutRequestDTO dto) {
        Function<AtivoPostPutRequestDTO, Ativo> builder = builders.get(dto.getTipo());
        if (builder == null) {
            throw new IllegalArgumentException("Tipo de ativo inválido: " + dto.getTipo());
        }
        return builder.apply(dto);
    }


}

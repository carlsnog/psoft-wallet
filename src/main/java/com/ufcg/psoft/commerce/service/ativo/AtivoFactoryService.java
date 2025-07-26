package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoUpsertDTO;
import com.ufcg.psoft.commerce.model.Ativo;

public interface AtivoFactoryService {
    Ativo criarAtivo(AtivoUpsertDTO dto);
}

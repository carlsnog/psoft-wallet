package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.model.Ativo;

public interface AtivoFactoryService {
    Ativo criarAtivo(AtivoPostPutRequestDTO dto);
}

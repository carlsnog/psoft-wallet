package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.dto.AtivoResponseDTO;

import java.util.List;

public interface AtivoService {

    AtivoResponseDTO criar(AtivoPostPutRequestDTO dto);

    AtivoResponseDTO atualizar(Long id, AtivoPostPutRequestDTO dto);

    void remover(Long id);

    AtivoResponseDTO buscarPorId(Long id);

    List<AtivoResponseDTO> listarTodos();

}

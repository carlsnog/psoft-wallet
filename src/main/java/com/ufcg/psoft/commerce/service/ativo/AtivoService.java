package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.AtivoUpsertDTO;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.model.Usuario;
import java.util.List;

public interface AtivoService {
  AtivoResponseDTO criar(AtivoUpsertDTO dto);

  AtivoResponseDTO atualizar(Long id, AtivoUpsertDTO dto);

  void remover(Long id);

  AtivoResponseDTO buscarPorId(Long id);

  List<AtivoResponseDTO> listar(Usuario usuario);

  AtivoResponseDTO alterarStatus(Long id, StatusAtivo novoStatus);
}

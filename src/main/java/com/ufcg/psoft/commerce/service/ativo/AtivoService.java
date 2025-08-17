package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoCreateDTO;
import com.ufcg.psoft.commerce.dto.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.AtivoUpdateDTO;
import com.ufcg.psoft.commerce.dto.CotacaoUpsertDTO;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Usuario;
import java.util.List;

public interface AtivoService {
  AtivoResponseDTO criar(AtivoCreateDTO dto);

  AtivoResponseDTO atualizar(Long id, AtivoUpdateDTO dto);

  AtivoResponseDTO atualizarCotacao(Long id, CotacaoUpsertDTO dto);

  void remover(Long id);

  Ativo getAtivo(Long id, Usuario usuario);

  Ativo getAtivoDisponivel(Long id, Usuario usuario);

  AtivoResponseDTO recuperar(Long id, Usuario usuario);

  List<AtivoResponseDTO> listar(Usuario usuario);

  AtivoResponseDTO alterarStatus(Long id, StatusAtivo novoStatus);
}

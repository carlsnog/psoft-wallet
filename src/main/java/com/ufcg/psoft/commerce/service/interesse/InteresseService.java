package com.ufcg.psoft.commerce.service.interesse;

import com.ufcg.psoft.commerce.dto.InteresseCreateDTO;
import com.ufcg.psoft.commerce.dto.InteresseResponseDTO;
import com.ufcg.psoft.commerce.model.Usuario;
import java.util.List;

public interface InteresseService {

  InteresseResponseDTO criar(InteresseCreateDTO interesseDto);

  void remover(Long id);

  InteresseResponseDTO buscarPorId(Long id);

  List<InteresseResponseDTO> listar(Usuario usuario);
}

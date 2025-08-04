package com.ufcg.psoft.commerce.service.interesse;

import com.ufcg.psoft.commerce.dto.InteresseCreateDTO;
import com.ufcg.psoft.commerce.dto.InteresseResponseDTO;
import com.ufcg.psoft.commerce.model.Usuario;
import java.util.List;

public interface InteresseService {

  InteresseResponseDTO criar(InteresseCreateDTO interesseDto, Usuario usuario);

  void remover(Long id, Usuario usuario);

  InteresseResponseDTO buscarPorId(Long id, Usuario usuario);

  List<InteresseResponseDTO> listar();
}

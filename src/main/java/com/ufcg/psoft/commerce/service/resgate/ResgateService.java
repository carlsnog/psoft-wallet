package com.ufcg.psoft.commerce.service.resgate;

import com.ufcg.psoft.commerce.dto.ResgateConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.ResgateCreateDTO;
import com.ufcg.psoft.commerce.dto.ResgateFilterDTO;
import com.ufcg.psoft.commerce.dto.ResgateResponseDTO;
import com.ufcg.psoft.commerce.model.Usuario;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ResgateService {

  ResgateResponseDTO criar(Usuario usuario, ResgateCreateDTO dto);

  ResgateResponseDTO recuperar(Usuario usuario, Long id);

  List<ResgateResponseDTO> listar(Usuario usuario);

  ResgateResponseDTO confirmar(Usuario usuario, Long id, ResgateConfirmacaoDTO dto);

  Page<ResgateResponseDTO> listar(Usuario usuario, ResgateFilterDTO filter, Pageable pageable);
}

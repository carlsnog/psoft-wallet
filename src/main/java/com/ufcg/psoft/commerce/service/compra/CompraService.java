package com.ufcg.psoft.commerce.service.compra;

import com.ufcg.psoft.commerce.dto.CompraConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.CompraCreateDTO;
import com.ufcg.psoft.commerce.dto.CompraFilterDTO;
import com.ufcg.psoft.commerce.dto.CompraResponseDTO;
import com.ufcg.psoft.commerce.model.Usuario;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompraService {

  CompraResponseDTO criar(Usuario usuario, CompraCreateDTO dto);

  CompraResponseDTO recuperar(Usuario usuario, Long id);

  List<CompraResponseDTO> listar(Usuario usuario);

  CompraResponseDTO confirmar(Usuario usuario, Long id, CompraConfirmacaoDTO dto);

  CompraResponseDTO liberarDisponibilidade(Usuario adm, Long id, CompraConfirmacaoDTO dto);

  Page<CompraResponseDTO> listar(Usuario usuario, CompraFilterDTO filter, Pageable pageable);
}

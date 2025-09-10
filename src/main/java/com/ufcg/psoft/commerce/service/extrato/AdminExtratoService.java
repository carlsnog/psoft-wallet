package com.ufcg.psoft.commerce.service.extrato;

import com.ufcg.psoft.commerce.dto.OperacaoFilterDTO;
import com.ufcg.psoft.commerce.dto.OperacaoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminExtratoService {
  Page<OperacaoResponseDTO> buscarOperacoes(OperacaoFilterDTO filter, Pageable pageable);
}

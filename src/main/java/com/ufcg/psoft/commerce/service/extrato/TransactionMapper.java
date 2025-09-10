package com.ufcg.psoft.commerce.service.extrato;

import com.ufcg.psoft.commerce.dto.CompraResponseDTO;
import com.ufcg.psoft.commerce.dto.ResgateResponseDTO;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import java.math.BigDecimal;

public class TransactionMapper {
  public static CompraResponseDTO toCompraResponseDTO(Compra c) {
    CompraResponseDTO dto = new CompraResponseDTO();
    dto.setId(c.getId());
    dto.setAtivoId(c.getAtivo().getId());
    dto.setValorUnitario(c.getValorUnitario());
    dto.setQuantidade(c.getQuantidade());
    dto.setValorTotal(c.getValorUnitario().multiply(BigDecimal.valueOf(c.getQuantidade())));
    dto.setStatus(c.getStatus());
    dto.setAbertaEm(c.getAbertaEm());
    dto.setFinalizadaEm(c.getFinalizadaEm());
    return dto;
  }

  public static ResgateResponseDTO toResgateResponseDTO(Resgate r) {
    ResgateResponseDTO dto = new ResgateResponseDTO();
    dto.setId(r.getId());
    dto.setAtivoId(r.getAtivo().getId());
    dto.setValorTotal(r.getValorTotal());
    dto.setStatus(r.getStatus());
    dto.setSolicitadoEm(r.getFinalizadaEm());
    dto.setFinalizadoEm(r.getFinalizadaEm());
    return dto;
  }
}

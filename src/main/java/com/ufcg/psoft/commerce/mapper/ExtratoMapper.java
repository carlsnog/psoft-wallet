package com.ufcg.psoft.commerce.mapper;

import com.ufcg.psoft.commerce.dto.ExtratoDTO;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import java.math.BigDecimal;

public class ExtratoMapper {

  public static ExtratoDTO fromCompra(Compra c) {
    var total = c.getValorUnitario().multiply(BigDecimal.valueOf(c.getQuantidade()));
    return new ExtratoDTO(
        "Compra",
        c.getAtivo().getNome(),
        c.getQuantidade(),
        c.getValorUnitario(),
        total,
        BigDecimal.ZERO,
        BigDecimal.ZERO,
        c.getAbertaEm(),
        c.getFinalizadaEm(),
        c.getStatus().name());
  }

  public static ExtratoDTO fromResgate(Resgate r) {
    var total = r.getValorUnitario().multiply(BigDecimal.valueOf(r.getQuantidade()));
    return new ExtratoDTO(
        "Resgate",
        r.getAtivo().getNome(),
        r.getQuantidade(),
        r.getValorUnitario(),
        total,
        r.getLucro(),
        r.getImpostoPago(),
        r.getAbertaEm(),
        r.getFinalizadaEm(),
        r.getStatus().name());
  }
}

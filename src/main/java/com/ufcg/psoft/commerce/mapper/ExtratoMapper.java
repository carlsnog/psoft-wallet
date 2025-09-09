package com.ufcg.psoft.commerce.mapper;

import com.ufcg.psoft.commerce.dto.ExtratoDTO;
import com.ufcg.psoft.commerce.model.transacao.Transacao;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import java.math.BigDecimal;

public class ExtratoMapper {

  public static ExtratoDTO fromTransacao(Transacao transacao) {
    BigDecimal total =
        transacao.getValorUnitario().multiply(BigDecimal.valueOf(transacao.getQuantidade()));

    return new ExtratoDTO(
        transacao.getTipoTransacao(),
        transacao.getAtivo().getNome(),
        transacao.getQuantidade(),
        transacao.getValorUnitario(),
        total,
        transacao.getLucro(),
        transacao.getImpostoPago(),
        transacao.getAbertaEm(),
        transacao.getFinalizadaEm());
  }

  public static ExtratoDTO fromCompra(Compra compra) {
    return fromTransacao(compra);
  }

  public static ExtratoDTO fromResgate(Resgate resgate) {
    return fromTransacao(resgate);
  }
}

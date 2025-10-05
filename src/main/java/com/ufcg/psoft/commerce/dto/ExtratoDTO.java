package com.ufcg.psoft.commerce.dto;

import com.ufcg.psoft.commerce.model.transacao.Transacao;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ExtratoDTO(
    String tipo,
    String ativo,
    int quantidade,
    BigDecimal valorUnitario,
    BigDecimal total,
    BigDecimal lucro,
    BigDecimal impostoPago,
    LocalDateTime abertaEm,
    LocalDateTime finalizadaEm) {

  public static ExtratoDTO fromTransacao(Transacao transacao) {
    return ExtratoDTO.builder()
        .tipo(transacao.getTipoTransacao())
        .ativo(transacao.getAtivo().getNome())
        .quantidade(transacao.getQuantidade())
        .valorUnitario(transacao.getValorUnitario())
        .total(transacao.getValorTotal())
        .lucro(transacao.getLucro())
        .impostoPago(transacao.getImpostoPago())
        .abertaEm(transacao.getAbertaEm())
        .finalizadaEm(transacao.getFinalizadaEm())
        .build();
  }
}

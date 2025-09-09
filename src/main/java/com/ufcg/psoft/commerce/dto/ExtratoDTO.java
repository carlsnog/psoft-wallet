package com.ufcg.psoft.commerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExtratoDTO(
    String tipo,
    String ativo,
    int quantidade,
    BigDecimal valorUnitario,
    BigDecimal total,
    BigDecimal lucro,
    BigDecimal impostoPago,
    LocalDateTime abertaEm,
    LocalDateTime finalizadaEm) {}

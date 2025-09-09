package com.ufcg.psoft.commerce.util;

import com.ufcg.psoft.commerce.dto.ExtratoDTO;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CsvExporter {

  public static byte[] gerarCsv(List<ExtratoDTO> linhas) {
    StringBuilder sb = new StringBuilder();
    sb.append(
        "Tipo,Ativo,Quantidade,Valor Unitário,Total,Lucro,Imposto Pago,Aberta Em,Finalizada Em,Status\n");

    for (ExtratoDTO l : linhas) {
      sb.append(l.tipo())
          .append(",")
          .append(l.ativo())
          .append(",")
          .append(l.quantidade())
          .append(",")
          .append(l.valorUnitario())
          .append(",")
          .append(l.total())
          .append(",")
          .append(l.lucro() != null ? l.lucro() : BigDecimal.ZERO)
          .append(",")
          .append(l.impostoPago() != null ? l.impostoPago() : BigDecimal.ZERO)
          .append(",")
          .append(l.abertaEm())
          .append(",")
          .append(l.finalizadaEm() != null ? l.finalizadaEm() : "")
          .append(",")
          .append(l.status())
          .append("\n");
    }

    return sb.toString().getBytes(StandardCharsets.UTF_8);
  }
}

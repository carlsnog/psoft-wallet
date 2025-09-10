package com.ufcg.psoft.commerce.util;

import com.ufcg.psoft.commerce.dto.ExtratoDTO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvExporter {

  public static byte[] gerarCsv(List<ExtratoDTO> linhas) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        CSVPrinter csvPrinter =
            CSVFormat.DEFAULT
                .withHeader(
                    "Tipo",
                    "Ativo",
                    "Quantidade",
                    "Valor Unitario",
                    "Total",
                    "Lucro",
                    "Imposto Pago",
                    "Aberta Em",
                    "Finalizada Em")
                .print(writer)) {

      for (ExtratoDTO l : linhas) {
        csvPrinter.printRecord(
            l.tipo(),
            l.ativo(),
            l.quantidade(),
            l.valorUnitario(),
            l.total(),
            l.lucro() != null ? l.lucro() : BigDecimal.ZERO,
            l.impostoPago() != null ? l.impostoPago() : BigDecimal.ZERO,
            l.abertaEm(),
            l.finalizadaEm() != null ? l.finalizadaEm() : "");
      }

      csvPrinter.flush();
      return baos.toByteArray();

    } catch (IOException e) {
      throw new RuntimeException("Erro ao gerar CSV", e);
    }
  }
}

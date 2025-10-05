package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtratoFiltrosDTO {

  @JsonProperty("data_inicio")
  private LocalDateTime dataInicio;

  @JsonProperty("data_fim")
  private LocalDateTime dataFim;

  @JsonProperty("tipo_ativo")
  private AtivoTipo tipoAtivo;

  @JsonProperty("tipo_operacao")
  @Pattern(regexp = "^(Compra|Resgate)$", message = "Tipo de operacao deve ser Compra ou Resgate")
  private String tipoOperacao;

  @Size(max = 100, message = "Nome do ativo deve ter no máximo 100 caracteres")
  private String nomeAtivo;

  private Long clienteId;

  // Validação e normalização no setter do nomeAtivo
  public void setNomeAtivo(String nomeAtivo) {
    if (nomeAtivo != null) {
      this.nomeAtivo = nomeAtivo.trim();
    }
    this.nomeAtivo = nomeAtivo;
  }

  // Validação de datas
  public void setDataInicio(LocalDateTime dataInicio) {
    this.dataInicio = dataInicio;
    validarDatas();
  }

  public void setDataFim(LocalDateTime dataFim) {
    this.dataFim = dataFim;
    validarDatas();
  }

  private void validarDatas() {
    if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
      throw new IllegalArgumentException("Data de início não pode ser posterior à data de fim");
    }
  }
}

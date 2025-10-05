package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.transacao.resgate.ResgateStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResgateConfirmacaoDTO {

  @JsonProperty("statusAtual")
  @NotNull(message = "Status atual é obrigatório")
  private ResgateStatusEnum statusAtual;
}

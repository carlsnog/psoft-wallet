package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufcg.psoft.commerce.model.AtivoCarteira;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarteiraResponseDTO {
  @JsonProperty("saldo")
  private BigDecimal saldo;

  @JsonProperty("lucro_total")
  private BigDecimal lucroTotal;

  @JsonProperty("quantidade_ativos")
  private int quantidadeAtivos;

  @JsonProperty("ativos")
  private List<CarteiraAtivoResponseDTO> ativos;

  public CarteiraResponseDTO(List<AtivoCarteira> ativosCarteira) {
    var ativosMap = new HashMap<Long, CarteiraAtivoResponseDTO>();
    for (var ativoCarteira : ativosCarteira) {
      var ativoId = ativoCarteira.getAtivo().getId();

      if (!ativosMap.containsKey(ativoId)) {
        ativosMap.put(ativoId, new CarteiraAtivoResponseDTO(ativoCarteira));
        continue;
      }

      ativosMap.get(ativoId).adicionar(ativoCarteira);
    }

    this.ativos = ativosMap.values().stream().collect(Collectors.toList());
    this.saldo =
        ativos.stream()
            .map(CarteiraAtivoResponseDTO::getValorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    this.lucroTotal =
        ativos.stream()
            .map(CarteiraAtivoResponseDTO::getLucro)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    this.quantidadeAtivos = ativos.size();
  }
}

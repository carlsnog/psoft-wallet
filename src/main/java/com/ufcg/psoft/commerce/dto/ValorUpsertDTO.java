package com.ufcg.psoft.commerce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ValorUpsertDTO {

  @JsonProperty("valor")
  @Digits(
      integer = 15,
      fraction = 2,
      message = "Valor deve ter no máximo 15 dígitos inteiros e 2 decimais")
  @DecimalMin(
      value = "0.01",
      inclusive = true,
      message = "Valor deve ser positivo e maior que zero")
  @NotNull(message = "Valor é obrigatório")
  private BigDecimal valor;
}

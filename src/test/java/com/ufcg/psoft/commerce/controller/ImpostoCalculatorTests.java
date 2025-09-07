package com.ufcg.psoft.commerce.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.service.imposto.ImpostoAcaoStrategy;
import com.ufcg.psoft.commerce.service.imposto.ImpostoCalculator;
import com.ufcg.psoft.commerce.service.imposto.ImpostoCriptoStrategy;
import com.ufcg.psoft.commerce.service.imposto.ImpostoTesouroDiretoStrategy;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImpostoCalculatorTests {

  private ImpostoCalculator calculator;

  @BeforeEach
  void setup() {
    calculator =
        new ImpostoCalculator(
            List.of(
                new ImpostoAcaoStrategy(),
                new ImpostoCriptoStrategy(),
                new ImpostoTesouroDiretoStrategy()));
  }

  @Test
  @DisplayName("Deve calcular imposto de Ação (15%)")
  void deveCalcularImpostoAcao() {
    BigDecimal lucro = BigDecimal.valueOf(1000);
    BigDecimal imposto = calculator.calcular(AtivoTipo.ACAO, lucro);
    assertEquals(new BigDecimal("150.00"), imposto.setScale(2));
  }

  @Test
  @DisplayName("Deve calcular imposto de Cripto até o teto (15%)")
  void deveCalcularImpostoCriptoAteTeto() {
    BigDecimal lucro = BigDecimal.valueOf(3000);
    BigDecimal imposto = calculator.calcular(AtivoTipo.CRIPTO, lucro);
    assertEquals(new BigDecimal("450.00"), imposto.setScale(2));
  }

  @Test
  @DisplayName("Deve calcular imposto de Cripto acima do teto (25%)")
  void deveCalcularImpostoCriptoAcimaTeto() {
    BigDecimal lucro = BigDecimal.valueOf(10000);
    BigDecimal imposto = calculator.calcular(AtivoTipo.CRIPTO, lucro);
    assertEquals(new BigDecimal("2500.00"), imposto.setScale(2));
  }

  @Test
  @DisplayName("Deve calcular imposto de Tesouro Direto (10%)")
  void deveCalcularImpostoTesouroDireto() {
    BigDecimal lucro = BigDecimal.valueOf(2000);
    BigDecimal imposto = calculator.calcular(AtivoTipo.TESOURO, lucro);
    assertEquals(new BigDecimal("200.00"), imposto.setScale(2));
  }

  @Test
  @DisplayName("Deve retornar zero quando lucro é negativo")
  void deveRetornarZeroQuandoLucroNegativo() {
    BigDecimal imposto = calculator.calcular(AtivoTipo.ACAO, BigDecimal.valueOf(-500));
    assertEquals(BigDecimal.ZERO, imposto);
  }

  @Test
  @DisplayName("Deve retornar zero quando lucro é zero")
  void deveRetornarZeroQuandoLucroZero() {
    BigDecimal imposto = calculator.calcular(AtivoTipo.CRIPTO, BigDecimal.ZERO);
    assertEquals(BigDecimal.ZERO, imposto);
  }

  @Test
  @DisplayName("Deve lançar exceção para tipo de ativo inválido")
  void deveLancarExcecaoParaTipoInvalido() {
    CommerceException ex =
        assertThrows(
            CommerceException.class, () -> calculator.calcular(null, BigDecimal.valueOf(100)));
    assertEquals(ErrorCode.TIPO_ATIVO_INVALIDO, ex.getErrorDTO().getCode());
  }
}

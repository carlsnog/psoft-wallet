package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.enums.*;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Ativo {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, unique = true)
  private String nome;

  @Column(nullable = false, length = 500)
  private String descricao;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StatusAtivo status;

  // TODO mudar para cotação
  @Column(nullable = false, scale = 2, precision = 19)
  private BigDecimal cotacao;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AtivoTipo tipo;

  public void atualizarCotacao(BigDecimal novaCotacao) {
    BigDecimal diferenca = novaCotacao.subtract(cotacao);
    diferenca = diferenca.abs();

    BigDecimal taxa = diferenca.divide(cotacao, 16, RoundingMode.HALF_UP);

    if (taxa.compareTo(BigDecimal.valueOf(0.01)) < 0)
      throw new CommerceException(ErrorCode.ATUALIZA_COTACAO_NAO_ATENDE_REQUISITO);

    this.cotacao = novaCotacao;
  }
}

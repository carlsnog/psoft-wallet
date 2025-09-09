package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ativo_carteira")
public class AtivoCarteira {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  public BigDecimal getValorUnitario() {
    return this.ativo.getCotacao();
  }

  public BigDecimal getValor() {
    return this.ativo.getCotacao().multiply(BigDecimal.valueOf(this.quantidade));
  }

  public BigDecimal getLucro() {
    return (this.ativo.getCotacao().subtract(this.compra.getValorUnitario()))
        .multiply(BigDecimal.valueOf(this.quantidade));
  }

  @ManyToOne
  @JoinColumn(name = "ativo_id")
  private Ativo ativo;

  @Column(nullable = false)
  private int quantidade;

  @ManyToOne
  @JoinColumn(name = "cliente_id")
  private Cliente cliente;

  @ManyToOne(optional = false)
  @JoinColumn(name = "compra_id", nullable = false)
  private Compra compra;
}

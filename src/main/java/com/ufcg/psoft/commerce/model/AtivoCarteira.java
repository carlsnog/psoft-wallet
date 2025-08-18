package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.model.compra.Compra;
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

  public BigDecimal getValor() {
    return this.ativo.getCotacao();
  }

  public BigDecimal getLucro() {
    return this.ativo.getCotacao().subtract(this.compra.getValorUnitario());
  }

  @ManyToOne
  @JoinColumn(name = "ativo_id")
  private Ativo ativo;

  @ManyToOne
  @JoinColumn(name = "cliente_id")
  private Cliente cliente;

  @ManyToOne(optional = false)
  @JoinColumn(name = "compra_id", nullable = false)
  private Compra compra;
}

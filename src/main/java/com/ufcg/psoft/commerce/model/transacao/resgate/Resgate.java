package com.ufcg.psoft.commerce.model.transacao.resgate;

import com.ufcg.psoft.commerce.model.transacao.Transacao;
import com.ufcg.psoft.commerce.model.transacao.resgate.state.ResgateState;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue("RESGATE")
public class Resgate extends Transacao {

  @Column(nullable = false)
  @Builder.Default
  @Enumerated(EnumType.STRING)
  private ResgateStatusEnum status = ResgateStatusEnum.SOLICITADO;

  @Column(precision = 19, scale = 2)
  private BigDecimal lucro;

  @Column(precision = 19, scale = 2)
  private BigDecimal impostoPago;

  @Override
  protected ResgateState getState() {
    return this.status.getState(this);
  }

  @Override
  public String getTipoTransacao() {
    return "Resgate";
  }

  @Override
  public BigDecimal getLucro() {
    return this.lucro;
  }

  @Override
  public BigDecimal getImpostoPago() {
    return this.impostoPago;
  }
}

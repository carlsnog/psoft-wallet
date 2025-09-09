package com.ufcg.psoft.commerce.model.transacao.compra;

import com.ufcg.psoft.commerce.model.AtivoCarteira;
import com.ufcg.psoft.commerce.model.transacao.Transacao;
import com.ufcg.psoft.commerce.model.transacao.compra.state.CompraState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("COMPRA")
public class Compra extends Transacao {

  @Column(nullable = false)
  @Builder.Default
  @Enumerated(EnumType.STRING)
  private CompraStatusEnum status = CompraStatusEnum.SOLICITADO;

  @Override
  protected CompraState getState() {
    return this.status.getState(this);
  }

  @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<AtivoCarteira> ativoCarteiras;
}

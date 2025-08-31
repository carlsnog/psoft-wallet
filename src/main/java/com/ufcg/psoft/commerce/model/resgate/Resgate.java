package com.ufcg.psoft.commerce.model.resgate;

import com.ufcg.psoft.commerce.enums.ResgateStatusEnum;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.resgate.state.ResgateState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
@Table(name = "resgate")
public class Resgate {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, scale = 2, precision = 19)
  private BigDecimal valorUnitario;

  @Column(nullable = false)
  private int quantidade;

  public BigDecimal getValorTotal() {
    return this.valorUnitario.multiply(BigDecimal.valueOf(this.quantidade));
  }

  @Column(nullable = false)
  private LocalDateTime solicitadoEm;

  @Column(nullable = true)
  private LocalDateTime finalizadoEm;

  @Column(nullable = false)
  @Builder.Default
  @Enumerated(EnumType.STRING)
  private ResgateStatusEnum status = ResgateStatusEnum.SOLICITADO;

  private ResgateState getState() {
    return this.status.getState(this);
  }

  public void confirmar(Usuario usuario) {
    getState().confirmar(usuario);
  }

  public boolean deveFinalizar() {
    return getState().deveFinalizar();
  }

  public void finalizar() {
    this.finalizadoEm = LocalDateTime.now();
  }

  public boolean isFinalizado() {
    return this.finalizadoEm != null;
  }

  @ManyToOne
  @JoinColumn(name = "cliente_id", referencedColumnName = "id")
  private Cliente cliente;

  @ManyToOne
  @JoinColumn(name = "ativo_id", referencedColumnName = "id")
  private Ativo ativo;

  public Long getClienteId() {
    return cliente.getId();
  }

  public Long getAtivoId() {
    return ativo.getId();
  }
}

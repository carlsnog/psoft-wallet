package com.ufcg.psoft.commerce.model.transacao;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transacao")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_transacao")
public abstract class Transacao {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, scale = 2, precision = 19)
  private BigDecimal valorUnitario;

  @Column(nullable = false)
  private int quantidade;

  @Column(nullable = false)
  private LocalDateTime abertaEm;

  @Column(nullable = true)
  private LocalDateTime finalizadaEm;

  @ManyToOne
  @JoinColumn(name = "cliente_id")
  private Cliente cliente;

  @ManyToOne
  @JoinColumn(name = "ativo_id")
  private Ativo ativo;

  public BigDecimal getValorTotal() {
    return this.valorUnitario.multiply(BigDecimal.valueOf(this.quantidade));
  }

  protected abstract TransacaoState getState();

  public abstract String getTipoTransacao();

  public final void confirmar(Usuario usuario) {
    getState().confirmar(usuario);
  }

  public final boolean deveFinalizar() {
    return getState().deveFinalizar();
  }

  public final void finalizar() {
    this.finalizadaEm = LocalDateTime.now();
  }

  public final boolean isFinalizado() {
    return this.finalizadaEm != null;
  }

  public final Long getClienteId() {
    return cliente.getId();
  }

  public final Long getAtivoId() {
    return ativo.getId();
  }

  public BigDecimal getLucro() {
    return BigDecimal.ZERO;
  }

  public BigDecimal getImpostoPago() {
    return BigDecimal.ZERO;
  }
}

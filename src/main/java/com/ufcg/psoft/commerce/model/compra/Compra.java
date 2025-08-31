package com.ufcg.psoft.commerce.model.compra;

import com.ufcg.psoft.commerce.enums.CompraStatusEnum;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.AtivoCarteira;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.compra.state.CompraState;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
@Table(name = "compra")
public class Compra {

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
  private LocalDateTime abertaEm;

  @Column(nullable = true)
  private LocalDateTime finalizadaEm;

  @Column(nullable = false)
  @Builder.Default
  @Enumerated(EnumType.STRING)
  private CompraStatusEnum status = CompraStatusEnum.SOLICITADO;

  private CompraState getState() {
    return this.status.getState(this);
  }

  public void confirmar(Usuario usuario) {
    getState().confirmar(usuario);
  }

  public boolean deveFinalizar() {
    return getState().deveFinalizar();
  }

  public void finalizar() {
    this.finalizadaEm = LocalDateTime.now();
  }

  public boolean isFinalizada() {
    return this.finalizadaEm != null;
  }

  @ManyToOne
  @JoinColumn(name = "cliente_id", referencedColumnName = "id")
  private Cliente cliente;

  @ManyToOne
  @JoinColumn(name = "ativo_id", referencedColumnName = "id")
  private Ativo ativo;

  @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<AtivoCarteira> ativoCarteiras;

  public Long getClienteId() {
    return cliente.getId();
  }

  public Long getAtivoId() {
    return ativo.getId();
  }
}

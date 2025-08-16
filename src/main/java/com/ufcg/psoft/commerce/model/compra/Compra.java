package com.ufcg.psoft.commerce.model.compra;

import com.ufcg.psoft.commerce.enums.CompraStatusEnum;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.compra.state.CompraState;
import com.ufcg.psoft.commerce.model.compra.state.CompraStateFactory;
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
import jakarta.persistence.Transient;
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
@Table(name = "compra")
public class Compra {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false, scale = 2, precision = 19)
  private BigDecimal valor;

  @Column(nullable = false)
  private int quantidade;

  @Column(nullable = false)
  private LocalDateTime abertaEm;

  @Column(nullable = true)
  private LocalDateTime finalizadaEm;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CompraStatusEnum status;

  @Transient private CompraState state;

  public CompraState getState() {
    if (state == null) {
      state = CompraStateFactory.getState(this);
    }
    return state;
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

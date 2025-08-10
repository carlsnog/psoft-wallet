package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import jakarta.persistence.*;
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
@Table(name = "interesse")
public class Interesse {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(nullable = false)
  private TipoInteresseEnum tipo;

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

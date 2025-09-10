package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.model.transacao.Transacao;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import jakarta.persistence.*;
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
@Table(name = "cliente")
public class Cliente extends Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(nullable = false)
  private String codigoAcesso;

  @Getter
  @Column(nullable = false)
  private String nome;

  @Getter
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private PlanoEnum plano;

  @Getter
  @Column(nullable = false)
  private String endereco;

  public int getSaldo(long ativoId) {
    return carteira.stream()
        .filter(ac -> ac.getAtivo().getId().equals(ativoId))
        .mapToInt(AtivoCarteira::getQuantidade)
        .sum();
  }

  @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Interesse> interesses;

  @OneToMany(
      mappedBy = "cliente",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      orphanRemoval = true)
  private List<AtivoCarteira> carteira;

  @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Transacao> transacoes;

  @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Compra> compras;

  @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Resgate> resgates;

  @Override
  public String getUserId() {
    return String.valueOf(id);
  }

  @Override
  public boolean isAdmin() {
    return false;
  }
}

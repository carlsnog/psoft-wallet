package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.model.compra.Compra;
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

  @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Interesse> interesses;

  @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<AtivoCarteira> carteira;

  @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Compra> compras;

  @Override
  public String getUserId() {
    return String.valueOf(id);
  }

  @Override
  public boolean isAdmin() {
    return false;
  }
}

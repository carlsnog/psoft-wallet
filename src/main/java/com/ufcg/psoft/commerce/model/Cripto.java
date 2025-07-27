package com.ufcg.psoft.commerce.model;

import com.ufcg.psoft.commerce.enums.*;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
public class Cripto extends Ativo {

  @Builder.Default private final AtivoTipo tipo = AtivoTipo.CRIPTO;
}

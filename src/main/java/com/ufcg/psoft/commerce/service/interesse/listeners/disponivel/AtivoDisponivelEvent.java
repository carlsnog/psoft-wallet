package com.ufcg.psoft.commerce.service.interesse.listeners.disponivel;

import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.service.interesse.listeners.AtivoBaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AtivoDisponivelEvent implements AtivoBaseEvent {
  private Ativo ativo;
}

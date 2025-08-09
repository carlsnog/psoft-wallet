package com.ufcg.psoft.commerce.service.interesse.listeners.disponivel;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.interesse.listeners.AtivoBaseEventListener;
import com.ufcg.psoft.commerce.service.interesse.notificacao.NotificacaoService;
import org.springframework.stereotype.Component;

@Component
public class AtivoDisponivelHandler extends AtivoBaseEventListener<AtivoDisponivelEvent> {

  public AtivoDisponivelHandler(
      InteresseRepository interesseRepository, NotificacaoService notificacaoService) {
    super(interesseRepository, notificacaoService);
  }

  @Override
  protected String formatarMensagem(AtivoDisponivelEvent event) {
    return "O ativo " + event.getAtivo().getNome() + " está disponível para compra!";
  }

  @Override
  protected TipoInteresseEnum getTipoInteresse() {
    return TipoInteresseEnum.DISPONIBILIDADE;
  }
}

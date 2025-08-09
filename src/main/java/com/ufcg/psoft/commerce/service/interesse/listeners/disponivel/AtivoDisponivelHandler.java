package com.ufcg.psoft.commerce.service.interesse.listeners.disponivel;

import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.interesse.listeners.AtivoBaseEventListener;
import com.ufcg.psoft.commerce.service.interesse.notificacao.NotificacaoService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AtivoDisponivelHandler extends AtivoBaseEventListener<AtivoDisponivelEvent> {

  public AtivoDisponivelHandler(
      InteresseRepository interesseRepository, NotificacaoService notificacaoService) {
    super(interesseRepository, notificacaoService);
  }

  @Override
  protected boolean deveNotificar(AtivoDisponivelEvent event) {
    var ativo = event.getAtivo();
    return ativo.getStatus() == StatusAtivo.DISPONIVEL;
  }

  @Override
  protected String formatarMensagem(AtivoDisponivelEvent event) {
    return "o ativo " + event.getAtivo().getNome() + " está disponível para compra!";
  }

  @Override
  protected TipoInteresseEnum getTipoInteresse() {
    return TipoInteresseEnum.DISPONIBILIDADE;
  }

  @Override
  protected void depoisDeNotificar(List<Interesse> interesses) {
    interesseRepository.deleteAll(interesses);
  }
}

package com.ufcg.psoft.commerce.service.interesse.listeners.preco;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.interesse.listeners.AtivoBaseEventListener;
import com.ufcg.psoft.commerce.service.interesse.notificacao.NotificacaoService;
import org.springframework.stereotype.Component;

@Component
public class AtivoPrecoHandler extends AtivoBaseEventListener<AtivoPrecoEvent> {

  public AtivoPrecoHandler(
      InteresseRepository interesseRepository, NotificacaoService notificacaoService) {
    super(interesseRepository, notificacaoService);
  }

  @Override
  protected String formatarMensagem(AtivoPrecoEvent event) {
    return "O preco do ativo "
        + event.getAtivo().getNome()
        + " foi atualizado para "
        + event.getNovoPreco()
        + "!";
  }

  @Override
  protected TipoInteresseEnum getTipoInteresse() {
    return TipoInteresseEnum.PRECO;
  }
}

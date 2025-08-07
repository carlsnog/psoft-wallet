package com.ufcg.psoft.commerce.event.listener;

import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.event.AtivoDisponivelEvent;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.interesse.NotificacaoService;
import java.util.List;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class AtivoDisponivelListener implements ApplicationListener<AtivoDisponivelEvent> {

  private final InteresseRepository interesseRepository;
  private final NotificacaoService notificacaoService;

  public AtivoDisponivelListener(
      InteresseRepository interesseRepository, NotificacaoService notificacaoService) {
    this.interesseRepository = interesseRepository;
    this.notificacaoService = notificacaoService;
  }

  @Override
  public void onApplicationEvent(AtivoDisponivelEvent event) {
    Ativo ativo = event.getAtivo();
    notificaInteressadosPorDisponibilidade(ativo);
  }

  private void notificaInteressadosPorDisponibilidade(Ativo ativo) {
    List<Interesse> interesses = buscarInteressesPorDisponibilidade(ativo);
    if (!interesses.isEmpty()) {
      notificacaoService.notificarDisponibilidade(ativo);
      interesseRepository.deleteAll(interesses);
    }
  }

  private List<Interesse> buscarInteressesPorDisponibilidade(Ativo ativo) {
    return interesseRepository.findByTipoAndAtivo_Id(
        TipoInteresseEnum.DISPONIBILIDADE, ativo.getId());
  }
}

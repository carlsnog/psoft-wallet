package com.ufcg.psoft.commerce.integracao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.ufcg.psoft.commerce.dto.InteresseCreateDTO;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.model.*;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.interesse.InteresseService;
import com.ufcg.psoft.commerce.service.interesse.notificacao.NotificacaoService;
import java.math.BigDecimal;
import java.util.Objects;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class DisponibilidadeNotificacaoTest {

  @Autowired AtivoRepository ativoRepository;
  @Autowired ClienteRepository clienteRepository;
  @Autowired InteresseRepository interesseRepository;
  @Autowired AtivoService ativoService;
  @Autowired InteresseService interesseService;

  @MockBean NotificacaoService notificacaoService;

  @AfterEach
  void tearDown() {
    interesseRepository.deleteAll();
    ativoRepository.deleteAll();
    clienteRepository.deleteAll();
    reset(notificacaoService);
  }

  @Test
  @DisplayName("Disponibilidade: Tesouro Direto deve notificar usuários normais interessados")
  void disponibilidadeTesouroDireto_notificaUsuarioNormal() {
    Cliente cliente = criarClienteNormal();
    Usuario usuario = cliente;
    var ativo = criarTesouroDiretoIndisponivel();

    criarInteresseDisponibilidade(usuario, cliente, ativo.getId());

    ativoService.alterarStatus(ativo.getId(), StatusAtivo.DISPONIVEL);

    verify(notificacaoService, times(1))
        .notificar(
            argThat(c -> Objects.equals(c.getId(), cliente.getId())), contains("está disponível"));
  }

  @Test
  @DisplayName(
      "Disponibilidade: com 2 usuários normais interessados no mesmo ativo, ambos devem ser notificados")
  void doisUsuariosNormaisMesmaDisponibilidade_ambosRecebemNotificacao() {
    var cliente1 = criarClienteNormal();
    var cliente2 = criarClienteNormal();
    Usuario u1 = cliente1;
    Usuario u2 = cliente2;

    var ativo = criarTesouroDiretoIndisponivel();

    criarInteresseDisponibilidade(u1, cliente1, ativo.getId());
    criarInteresseDisponibilidade(u2, cliente2, ativo.getId());

    ativoService.alterarStatus(ativo.getId(), StatusAtivo.DISPONIVEL);

    verify(notificacaoService, times(2))
        .notificar(
            argThat(
                cli ->
                    cli != null
                        && (Objects.equals(cli.getId(), cliente1.getId())
                            || Objects.equals(cli.getId(), cliente2.getId()))),
            contains("está disponível"));
  }

  @Test
  @DisplayName("Disponibilidade: Tesouro Direto deve notificar usuários premium interessados")
  void disponibilidadeTesouroDireto_notificaUsuarioPremium() {
    Cliente cliente = criarClientePremium();
    Usuario usuario = cliente;
    var ativo = criarTesouroDiretoIndisponivel();

    criarInteresseDisponibilidade(usuario, cliente, ativo.getId());

    ativoService.alterarStatus(ativo.getId(), StatusAtivo.DISPONIVEL);

    verify(notificacaoService, times(1))
        .notificar(
            argThat(c -> Objects.equals(c.getId(), cliente.getId())), contains("está disponível"));
  }

  @Test
  @DisplayName("Disponibilidade: CRIPTO deve notificar usuários premium interessados")
  void disponibilidadeCripto_notificaUsuarioPremium() {
    Cliente cliente = criarClientePremium();
    Usuario usuario = cliente;
    var ativo = criarCriptoIndisponivel();

    criarInteresseDisponibilidade(usuario, cliente, ativo.getId());

    ativoService.alterarStatus(ativo.getId(), StatusAtivo.DISPONIVEL);

    verify(notificacaoService, times(1))
        .notificar(
            argThat(c -> Objects.equals(c.getId(), cliente.getId())), contains("está disponível"));
  }

  @Test
  @DisplayName("Disponibilidade: ACAO deve notificar usuários premium interessados")
  void disponibilidadeAcao_notificaUsuarioPremium() {
    Cliente cliente = criarClientePremium();
    Usuario usuario = cliente;
    var ativo = criarAcaoIndisponivel();

    criarInteresseDisponibilidade(usuario, cliente, ativo.getId());

    ativoService.alterarStatus(ativo.getId(), StatusAtivo.DISPONIVEL);

    verify(notificacaoService, times(1))
        .notificar(
            argThat(c -> Objects.equals(c.getId(), cliente.getId())), contains("está disponível"));
  }

  @Test
  @DisplayName("Disponibilidade: ACAO deve falhar para users Normais")
  void userNormalTentaDisponibilidadeEmAcao() {
    Cliente cliente = criarClienteNormal();
    Usuario usuario = cliente;
    var ativo = criarAcaoIndisponivel();

    assertThrows(
        CommerceException.class,
        () -> {
          criarInteresseDisponibilidade(usuario, cliente, ativo.getId());
        });

    verify(notificacaoService, never()).notificar(any(), any());
  }

  @Test
  @DisplayName("Disponibilidade: sem interessados não deve notificar")
  void semInteressados_naoNotificaDisponibilidade() {
    var ativo = criarTesouroDiretoIndisponivel();

    // altera status para disponível sem criar interesse
    ativoService.alterarStatus(ativo.getId(), StatusAtivo.DISPONIVEL);

    verify(notificacaoService, never()).notificar(any(), anyString());
  }

  // ===== helpers =====

  private com.ufcg.psoft.commerce.model.Ativo criarTesouroDiretoIndisponivel() {
    return ativoRepository.save(
        Tesouro.builder()
            .nome("Tesouro Selic 2029")
            .descricao("Título Público")
            .cotacao(BigDecimal.valueOf(100.00))
            .status(StatusAtivo.INDISPONIVEL)
            .tipo(AtivoTipo.TESOURO)
            .build());
  }

  private com.ufcg.psoft.commerce.model.Ativo criarCriptoIndisponivel() {
    return ativoRepository.save(
        Tesouro.builder()
            .nome("Criptomoeda")
            .descricao("Título privado")
            .cotacao(BigDecimal.valueOf(100.00))
            .status(StatusAtivo.INDISPONIVEL)
            .tipo(AtivoTipo.CRIPTO)
            .build());
  }

  private com.ufcg.psoft.commerce.model.Ativo criarAcaoIndisponivel() {
    return ativoRepository.save(
        Tesouro.builder()
            .nome("Acao")
            .descricao("Título privado")
            .cotacao(BigDecimal.valueOf(250.00))
            .status(StatusAtivo.INDISPONIVEL)
            .tipo(AtivoTipo.ACAO)
            .build());
  }

  private void criarInteresseDisponibilidade(Usuario usuario, Cliente cliente, Long ativoId) {
    InteresseCreateDTO dto = new InteresseCreateDTO(cliente.getId(), ativoId);
    interesseService.criarInteresseDisponibilidade(usuario, dto);
  }

  private Cliente criarClientePremium() {
    return clienteRepository.save(
        Cliente.builder()
            .nome("Cliente Premium")
            .plano(PlanoEnum.PREMIUM)
            .endereco("Rua dos Testes Premium, 123")
            .codigoAcesso("123456")
            .build());
  }

  private Cliente criarClienteNormal() {
    return clienteRepository.save(
        Cliente.builder()
            .nome("Bernardo")
            .plano(PlanoEnum.NORMAL)
            .endereco("Rua dos Testes Normais, 123")
            .codigoAcesso("123456")
            .build());
  }
}

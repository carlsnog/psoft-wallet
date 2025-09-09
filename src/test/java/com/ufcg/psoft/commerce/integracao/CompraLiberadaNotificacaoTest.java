package com.ufcg.psoft.commerce.integracao;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ufcg.psoft.commerce.dto.CompraConfirmacaoDTO;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Admin;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.compra.CompraStatusEnum;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.compra.CompraService;
import com.ufcg.psoft.commerce.service.notificacao.NotificacaoService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Notificação - Compra Liberada")
public class CompraLiberadaNotificacaoTest {

  @Autowired CompraRepository compraRepository;
  @Autowired ClienteRepository clienteRepository;
  @Autowired AtivoService ativoService;
  @Autowired AtivoRepository ativoRepository;
  @Autowired CompraService compraService;

  @MockBean NotificacaoService notificacaoService;

  @AfterEach
  void tearDown() {
    compraRepository.deleteAll();

    reset(notificacaoService);
  }

  // Helper: cria e salva uma compra diretamente no repo (usa Ativo já existente)
  private Compra criarCompra(Cliente cliente, Ativo ativo, CompraStatusEnum status) {
    return compraRepository.save(
        Compra.builder()
            .cliente(cliente)
            .ativo(ativo)
            .quantidade(1)
            .valorUnitario(ativo.getCotacao())
            .abertaEm(LocalDateTime.now())
            .status(status)
            .build());
  }

  @Test
  @DisplayName("Admin libera compra SOLICITADO deve notificar o cliente dono da compra")
  void quandoAdminLiberaCompra_sinalizaNotificacaoAoCliente() {
    Cliente cliente =
        clienteRepository
            .findById(1L)
            .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
    Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance());

    Compra compra = criarCompra(cliente, ativo, CompraStatusEnum.SOLICITADO);

    CompraConfirmacaoDTO dto = new CompraConfirmacaoDTO();
    dto.setStatusAtual(CompraStatusEnum.SOLICITADO);

    // Act
    compraService.liberarDisponibilidade(Admin.getInstance(), compra.getId(), dto);

    // Assert: verifica que notificacaoService.notificar(...) foi chamado para o cliente correto
    verify(notificacaoService, times(1))
        .notificar(argThat(c -> c != null && c.getId() == cliente.getId()), contains("liberada"));
  }

  @Test
  @DisplayName("Ao liberar compra de um cliente, não notificar outros clientes")
  void naoNotificaOutrosClientes() {
    // Arrange: cria dois clientes e duas compras (apenas libera a 1ª)
    Cliente cliente1 =
        clienteRepository
            .findById(1L)
            .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
    // opcional: criar outro cliente localmente (ou usar id=2 seed)
    Cliente cliente2 =
        clienteRepository
            .findById(2L)
            .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));

    Ativo ativo = ativoService.getAtivoDisponivel(1L, Admin.getInstance());

    Compra compra1 = criarCompra(cliente1, ativo, CompraStatusEnum.SOLICITADO);
    Compra compra2 = criarCompra(cliente2, ativo, CompraStatusEnum.SOLICITADO);

    CompraConfirmacaoDTO dto = new CompraConfirmacaoDTO();
    dto.setStatusAtual(CompraStatusEnum.SOLICITADO);

    // Act: liberar somente a compra1
    compraService.liberarDisponibilidade(Admin.getInstance(), compra1.getId(), dto);

    // Assert: apenas cliente1 deve ser notificado
    verify(notificacaoService, times(1))
        .notificar(argThat(c -> c != null && c.getId() == cliente1.getId()), contains("liberada"));

    // assegura que cliente2 não foi notificado
    verify(notificacaoService, times(0))
        .notificar(argThat(c -> c != null && c.getId() == cliente2.getId()), any());
  }
}

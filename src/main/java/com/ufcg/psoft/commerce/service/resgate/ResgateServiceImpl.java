package com.ufcg.psoft.commerce.service.resgate;

import com.ufcg.psoft.commerce.dto.*;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.AtivoCarteira;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import com.ufcg.psoft.commerce.model.transacao.resgate.ResgateStatusEnum;
import com.ufcg.psoft.commerce.repository.ResgateRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import com.ufcg.psoft.commerce.service.extrato.ResgateSpecifications;
import com.ufcg.psoft.commerce.service.extrato.TransactionMapper;
import com.ufcg.psoft.commerce.service.resgate.listeners.ResgateConfirmadoEvent;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ResgateServiceImpl implements ResgateService {

  private final ResgateRepository resgateRepository;
  private final AtivoService ativoService;
  private final ClienteService clienteService;
  private ApplicationEventPublisher eventPublisher;

  public ResgateServiceImpl(
      ResgateRepository resgateRepository,
      AtivoService ativoService,
      ClienteService clienteService) {
    this.resgateRepository = resgateRepository;
    this.ativoService = ativoService;
    this.clienteService = clienteService;
  }

  @Autowired
  public void setEventPublisher(ApplicationEventPublisher publisher) {
    this.eventPublisher = publisher;
  }

  @Override
  @Transactional
  public ResgateResponseDTO criar(Usuario usuario, ResgateCreateDTO dto) {
    if (usuario.isAdmin()) {
      throw new CommerceException(ErrorCode.ACAO_APENAS_CLIENTE_DONO);
    }
    var cliente = (Cliente) usuario;
    var carteira = clienteService.recuperarCarteira(usuario, cliente.getId());

    var ativo = ativoService.getAtivo(dto.getAtivoId(), usuario);

    validarSaldoCliente(carteira.getAtivos(), ativo, dto.getQuantidade());

    var resgate =
        Resgate.builder()
            .cliente(cliente)
            .ativo(ativo)
            .status(ResgateStatusEnum.SOLICITADO)
            .quantidade(dto.getQuantidade())
            .valorUnitario(ativo.getCotacao())
            .abertaEm(LocalDateTime.now())
            .build();

    resgateRepository.save(resgate);
    return new ResgateResponseDTO(resgate);
  }

  private void validarSaldoCliente(
      List<CarteiraAtivoResponseDTO> ativos, Ativo ativo, int quantidade) {
    var saldo =
        ativos.stream()
            .filter(a -> a.getAtivoId().equals(ativo.getId()))
            .mapToInt(CarteiraAtivoResponseDTO::getQuantidade)
            .findFirst()
            .orElse(0);

    if (saldo < quantidade) {
      throw new CommerceException(ErrorCode.SALDO_INSUFICIENTE);
    }
  }

  @Override
  public ResgateResponseDTO recuperar(Usuario usuario, Long id) {
    Resgate resgate;
    if (usuario.isAdmin()) {
      resgate =
          resgateRepository
              .findById(id)
              .orElseThrow(() -> new CommerceException(ErrorCode.RESGATE_NAO_ENCONTRADO));
    } else {
      var cliente = (Cliente) usuario;
      resgate =
          resgateRepository
              .findByIdAndCliente_Id(id, cliente.getId())
              .orElseThrow(() -> new CommerceException(ErrorCode.RESGATE_NAO_ENCONTRADO));
    }

    return new ResgateResponseDTO(resgate);
  }

  @Override
  public List<ResgateResponseDTO> listar(Usuario usuario) {
    Stream<Resgate> resgates;

    if (usuario.isAdmin()) {
      resgates = resgateRepository.findAll().stream();
    } else {
      var cliente = (Cliente) usuario;
      resgates = resgateRepository.findByCliente_Id(cliente.getId()).stream();
    }

    return resgates.map(ResgateResponseDTO::new).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public ResgateResponseDTO confirmar(Usuario usuario, Long id, ResgateConfirmacaoDTO dto) {
    var resgate =
        resgateRepository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.RESGATE_NAO_ENCONTRADO));

    if (resgate.getStatus() != dto.getStatusAtual()) {
      throw new CommerceException(ErrorCode.CONFLICT);
    }

    resgate.confirmar(usuario);

    if (resgate.deveFinalizar()) {
      var removidos = removerDaCarteira(resgate, usuario);

      calcularLucro(resgate, removidos);

      resgateRepository.save(resgate);
    }
    eventPublisher.publishEvent(new ResgateConfirmadoEvent(this, resgate));
    return new ResgateResponseDTO(resgate);
  }

  private void calcularLucro(Resgate resgate, List<AtivoCarteira> carteira) {
    var lucro = BigDecimal.ZERO;
    for (var ativoCarteira : carteira) {
      lucro = lucro.add(ativoCarteira.getLucro(resgate.getValorUnitario()));
    }
    var imposto = resgate.getAtivo().calcularImposto(lucro);

    resgate.setLucro(lucro);
    resgate.setImpostoPago(imposto);
  }

  private List<AtivoCarteira> removerDaCarteira(Resgate resgate, Usuario usuario) {
    var cliente = resgate.getCliente();
    var ativoId = resgate.getAtivo().getId();

    var qtdParaRemover = resgate.getQuantidade();
    var removidos = new ArrayList<AtivoCarteira>();

    var carteira =
        cliente.getCarteira().stream()
            .filter(ac -> ac.getAtivo().getId().equals(ativoId))
            .collect(Collectors.toList());

    // Remove os ativos usando um algoritmo de FIFO para garantir a rotação dos ativoCarteira
    carteira.sort(
        (a, b) -> a.getCompra().getFinalizadaEm().compareTo(b.getCompra().getFinalizadaEm()));

    for (var ativoCarteira : carteira) {
      if (ativoCarteira.getQuantidade() > qtdParaRemover) {
        ativoCarteira.setQuantidade(ativoCarteira.getQuantidade() - qtdParaRemover);

        var removido = ativoCarteira.clone();
        removido.setQuantidade(qtdParaRemover);
        removidos.add(removido);

        qtdParaRemover = 0;
        break;
      }

      qtdParaRemover -= ativoCarteira.getQuantidade();
      removidos.add(ativoCarteira);
      cliente.getCarteira().remove(ativoCarteira);
    }

    if (qtdParaRemover > 0) {
      throw new CommerceException(ErrorCode.SALDO_INSUFICIENTE);
    }

    resgate.confirmar(usuario);
    resgate.finalizar();

    return removidos;
  }

  @Override
  public Page<ResgateResponseDTO> listar(
      Usuario usuario, ResgateFilterDTO filter, Pageable pageable) {
    if (!(usuario.isAdmin())) {
      Cliente cliente = (Cliente) usuario;
      if (filter == null) filter = new ResgateFilterDTO();
      Specification<Resgate> base = ResgateSpecifications.byFilter(filter);
      Specification<Resgate> clienteSpec =
          (root, query, cb) -> cb.equal(root.get("cliente").get("id"), cliente.getId());
      Page<Resgate> page = resgateRepository.findAll(base.and(clienteSpec), pageable);
      return page.map(TransactionMapper::toResgateResponseDTO);
    } else {
      throw new CommerceException(ErrorCode.ACAO_APENAS_CLIENTE_DONO);
    }
  }
}

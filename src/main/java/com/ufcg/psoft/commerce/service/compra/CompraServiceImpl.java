package com.ufcg.psoft.commerce.service.compra;

import com.ufcg.psoft.commerce.dto.CompraConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.CompraCreateDTO;
import com.ufcg.psoft.commerce.dto.CompraFilterDTO;
import com.ufcg.psoft.commerce.dto.CompraResponseDTO;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.AtivoCarteira;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.compra.CompraStatusEnum;
import com.ufcg.psoft.commerce.repository.AtivoCarteiraRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.compra.listeners.liberada.CompraLiberadaEvent;
import com.ufcg.psoft.commerce.service.extrato.CompraSpecifications;
import com.ufcg.psoft.commerce.service.extrato.TransactionMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class CompraServiceImpl implements CompraService {

  private final CompraRepository compraRepository;
  private final AtivoCarteiraRepository ativoCarteiraRepository;
  private final AtivoService ativoService;
  private final ApplicationEventPublisher eventPublisher;

  public CompraServiceImpl(
      CompraRepository compraRepository,
      AtivoCarteiraRepository ativoCarteiraRepository,
      AtivoService ativoService,
      ApplicationEventPublisher eventPublisher) {
    this.compraRepository = compraRepository;
    this.ativoCarteiraRepository = ativoCarteiraRepository;
    this.ativoService = ativoService;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public CompraResponseDTO criar(Usuario usuario, CompraCreateDTO dto) {
    if (usuario.isAdmin()) {
      throw new CommerceException(ErrorCode.ACAO_APENAS_CLIENTE_DONO);
    }

    var cliente = (Cliente) usuario;
    var ativo = ativoService.getAtivoDisponivel(dto.getAtivoId(), usuario);

    if (ativo.getStatus() == StatusAtivo.INDISPONIVEL) {
      throw new CommerceException(ErrorCode.ATIVO_NAO_DISPONIVEL);
    }

    var compra =
        Compra.builder()
            .cliente(cliente)
            .ativo(ativo)
            .quantidade(dto.getQuantidade())
            .valorUnitario(ativo.getCotacao())
            .abertaEm(LocalDateTime.now())
            .build();

    compraRepository.save(compra);
    return new CompraResponseDTO(compra);
  }

  @Override
  public CompraResponseDTO recuperar(Usuario usuario, Long id) {
    Compra compra;
    if (usuario.isAdmin()) {
      compra =
          compraRepository
              .findById(id)
              .orElseThrow(() -> new CommerceException(ErrorCode.COMPRA_NAO_ENCONTRADA));
    } else {
      var cliente = (Cliente) usuario;
      compra =
          compraRepository
              .findByIdAndCliente_Id(id, cliente.getId())
              .orElseThrow(() -> new CommerceException(ErrorCode.COMPRA_NAO_ENCONTRADA));
    }

    return new CompraResponseDTO(compra);
  }

  @Override
  public List<CompraResponseDTO> listar(Usuario usuario) {
    Stream<Compra> compras;

    if (usuario.isAdmin()) {
      compras = compraRepository.findAll().stream();
    } else {
      var cliente = (Cliente) usuario;
      compras = compraRepository.findByCliente_Id(cliente.getId()).stream();
    }

    return compras.map(CompraResponseDTO::new).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public CompraResponseDTO confirmar(Usuario usuario, Long id, CompraConfirmacaoDTO dto) {
    if (usuario.isAdmin()) {
      throw new CommerceException(ErrorCode.ACAO_APENAS_CLIENTE_DONO);
    }
    var compra =
        compraRepository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.COMPRA_NAO_ENCONTRADA));

    if (compra.getStatus() != dto.getStatusAtual()) { // Nesse caso houve concorrência de requests
      throw new CommerceException(ErrorCode.CONFLICT);
    }

    if (compra.getStatus() != CompraStatusEnum.DISPONIVEL) {
      throw new CommerceException(ErrorCode.COMPRA_NAO_ESTA_DISPONIVEL);
    }

    compra.confirmar(usuario);

    if (compra.deveFinalizar()) {
      adicionarCompraNaCarteira(compra);
    }

    compraRepository.save(compra);

    return new CompraResponseDTO(compra);
  }

  private void adicionarCompraNaCarteira(Compra compra) {
    var cliente = compra.getCliente();
    var ativo = compra.getAtivo();

    var ativoCarteira =
        AtivoCarteira.builder()
            .ativo(ativo)
            .quantidade(compra.getQuantidade())
            .cliente(cliente)
            .compra(compra)
            .build();

    ativoCarteiraRepository.save(ativoCarteira);

    compra.confirmar(cliente);
    compra.finalizar();
  }

  @Transactional
  @Override
  public CompraResponseDTO liberarDisponibilidade(
      Usuario admin, Long id, CompraConfirmacaoDTO dto) {
    if (!admin.isAdmin()) {
      throw new CommerceException(ErrorCode.ACAO_APENAS_ADMIN);
    }
    var compra =
        compraRepository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.COMPRA_NAO_ENCONTRADA));

    if (compra.getStatus() != dto.getStatusAtual()) { // Nesse caso houve concorrência de requests
      throw new CommerceException(ErrorCode.CONFLICT);
    }

    if (compra.getStatus() != CompraStatusEnum.SOLICITADO) {
      throw new CommerceException(ErrorCode.COMPRA_NAO_ESTA_SOLICITADA);
    }

    compra.confirmar(admin);

    compraRepository.save(compra);

    eventPublisher.publishEvent(new CompraLiberadaEvent(this, compra));

    return new CompraResponseDTO(compra);
  }

  @Override
  public Page<CompraResponseDTO> listar(
      Usuario usuario, CompraFilterDTO filter, Pageable pageable) {
    if (!(usuario.isAdmin())) {

      Cliente cliente = (Cliente) usuario;

      if (filter == null) filter = new CompraFilterDTO();

      Specification<Compra> base = CompraSpecifications.byFilter(filter);
      Specification<Compra> clienteSpec =
          (root, query, cb) -> cb.equal(root.get("cliente").get("id"), cliente.getId());
      Specification<Compra> finalSpec = base.and(clienteSpec);
      Page<Compra> page = compraRepository.findAll(finalSpec, pageable);
      return page.map(TransactionMapper::toCompraResponseDTO);
    } else {
      throw new CommerceException(ErrorCode.ACAO_APENAS_CLIENTE_DONO);
    }
  }
}

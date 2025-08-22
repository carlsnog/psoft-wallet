package com.ufcg.psoft.commerce.service.compra;

import com.ufcg.psoft.commerce.dto.CompraConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.CompraCreateDTO;
import com.ufcg.psoft.commerce.dto.CompraResponseDTO;
import com.ufcg.psoft.commerce.enums.CompraStatusEnum;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.AtivoCarteira;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.compra.Compra;
import com.ufcg.psoft.commerce.repository.AtivoCarteiraRepository;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class CompraServiceImpl implements CompraService {

  private final CompraRepository compraRepository;
  private final AtivoCarteiraRepository ativoCarteiraRepository;
  private final AtivoService ativoService;

  public CompraServiceImpl(
      CompraRepository compraRepository,
      AtivoCarteiraRepository ativoCarteiraRepository,
      AtivoService ativoService) {
    this.compraRepository = compraRepository;
    this.ativoCarteiraRepository = ativoCarteiraRepository;
    this.ativoService = ativoService;
  }

  @Override
  public CompraResponseDTO criar(Usuario usuario, CompraCreateDTO dto) {
    if (usuario.isAdmin()) {
      throw new CommerceException(ErrorCode.ACAO_APENAS_CLIENTE_DONO_COMPRA);
    }

    var cliente = (Cliente) usuario;
    var ativo = ativoService.getAtivoDisponivel(dto.getAtivoId(), usuario);

    if (ativo.getStatus() == StatusAtivo.INDISPONIVEL) {
      throw new CommerceException(ErrorCode.ATIVO_NAO_DISPONIVEL);
    }

    BigDecimal valorTotal = ativo.getCotacao().multiply(BigDecimal.valueOf(dto.getQuantidade()));

    if (cliente.getSaldo().compareTo(valorTotal) < 0) {
          throw new CommerceException(ErrorCode.SALDO_INSUFICIENTE);
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
      throw new CommerceException(ErrorCode.ACAO_APENAS_CLIENTE_DONO_COMPRA);
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

    var ativosCarteira = new ArrayList<AtivoCarteira>();
    for (int i = 0; i < compra.getQuantidade(); i++) {
      var ativoCarteira =
          AtivoCarteira.builder().ativo(ativo).cliente(cliente).compra(compra).build();

      ativosCarteira.add(ativoCarteira);
    }

    ativoCarteiraRepository.saveAll(ativosCarteira);

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

    return new CompraResponseDTO(compra);
  }
}

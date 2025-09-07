package com.ufcg.psoft.commerce.service.resgate;

import com.ufcg.psoft.commerce.dto.CarteiraAtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.ResgateConfirmacaoDTO;
import com.ufcg.psoft.commerce.dto.ResgateCreateDTO;
import com.ufcg.psoft.commerce.dto.ResgateResponseDTO;
import com.ufcg.psoft.commerce.enums.ResgateStatusEnum;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.AtivoCarteira;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.resgate.Resgate;
import com.ufcg.psoft.commerce.repository.AtivoCarteiraRepository;
import com.ufcg.psoft.commerce.repository.ResgateRepository;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import com.ufcg.psoft.commerce.service.cliente.ClienteService;
import com.ufcg.psoft.commerce.service.imposto.ImpostoCalculator;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class ResgateServiceImpl implements ResgateService {

  private final ResgateRepository resgateRepository;
  private final AtivoCarteiraRepository ativoCarteiraRepository;
  private final AtivoService ativoService;
  private final ClienteService clienteService;
  private final ImpostoCalculator impostoCalculator;

  public ResgateServiceImpl(
      ResgateRepository resgateRepository,
      AtivoCarteiraRepository ativoCarteiraRepository,
      AtivoService ativoService,
      ClienteService clienteService,
      ImpostoCalculator impostoCalculator) {
    this.resgateRepository = resgateRepository;
    this.ativoCarteiraRepository = ativoCarteiraRepository;
    this.ativoService = ativoService;
    this.clienteService = clienteService;
    this.impostoCalculator = impostoCalculator;
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
            .solicitadoEm(LocalDateTime.now())
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

    var lucro = calcularLucroFIFO(resgate);
    var imposto =
        impostoCalculator
            .calcular(resgate.getAtivo().getTipo(), lucro)
            .setScale(2, RoundingMode.HALF_UP);

    resgate.setLucro(lucro.setScale(2, RoundingMode.HALF_UP));
    resgate.setImpostoPago(imposto);

    resgate.confirmar(usuario);
    resgateRepository.save(resgate);

    if (resgate.deveFinalizar()) {
      removerDaCarteira(resgate, usuario);
    }

    return new ResgateResponseDTO(resgate);
  }

  private BigDecimal calcularLucroFIFO(Resgate resgate) {
    var cliente = resgate.getCliente();
    var ativoId = resgate.getAtivo().getId();
    var qtdNecessaria = resgate.getQuantidade();

    var lotes =
        cliente.getCarteira().stream()
            .filter(ac -> ac.getAtivo().getId().equals(ativoId))
            .sorted(Comparator.comparing(ac -> ac.getCompra().getFinalizadaEm()))
            .collect(Collectors.toList());

    if (lotes.stream().mapToInt(AtivoCarteira::getQuantidade).sum() < qtdNecessaria)
      throw new CommerceException(ErrorCode.SALDO_INSUFICIENTE);

    BigDecimal custoTotal = BigDecimal.ZERO;
    int restante = qtdNecessaria;

    for (var lote : lotes) {
      if (restante == 0) break;
      int usar = Math.min(restante, lote.getQuantidade());
      var custoUnit = lote.getCompra().getValorUnitario();
      custoTotal = custoTotal.add(custoUnit.multiply(BigDecimal.valueOf(usar)));
      restante -= usar;
    }

    var precoVendaTotal = resgate.getValorUnitario().multiply(BigDecimal.valueOf(qtdNecessaria));
    var lucro = precoVendaTotal.subtract(custoTotal);

    return lucro.signum() > 0 ? lucro : BigDecimal.ZERO;
  }

  private void removerDaCarteira(Resgate resgate, Usuario usuario) {
    var cliente = resgate.getCliente();
    var qtdParaRemover = resgate.getQuantidade();

    var ativoId = resgate.getAtivo().getId();
    var ativos =
        cliente.getCarteira().stream()
            .filter(ac -> ac.getAtivo().getId().equals(ativoId))
            .collect(Collectors.toList());

    // Remove os ativos usando um algoritmo de FIFO para garantir a rotação dos ativoCarteira
    ativos.sort(
        (a, b) -> a.getCompra().getFinalizadaEm().compareTo(b.getCompra().getFinalizadaEm()));

    for (var ativoCarteira : ativos) {
      if (ativoCarteira.getQuantidade() > qtdParaRemover) {
        ativoCarteira.setQuantidade(ativoCarteira.getQuantidade() - qtdParaRemover);
        ativoCarteiraRepository.save(ativoCarteira);
        qtdParaRemover = 0;
        break;
      }

      qtdParaRemover -= ativoCarteira.getQuantidade();
      cliente.getCarteira().remove(ativoCarteira);
    }

    if (qtdParaRemover > 0) {
      throw new CommerceException(ErrorCode.SALDO_INSUFICIENTE);
    }

    resgate.confirmar(usuario);
    resgate.finalizar();
  }
}

package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.CarteiraResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;
import com.ufcg.psoft.commerce.dto.ExtratoDTO;
import com.ufcg.psoft.commerce.dto.ExtratoFiltrosDTO;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.transacao.Transacao;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.TransacaoRepository;
import com.ufcg.psoft.commerce.util.CsvExporter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ClienteServiceImpl implements ClienteService {

  private final ClienteRepository clienteRepository;
  private final TransacaoRepository transacaoRepository;
  private final ModelMapper modelMapper;

  public ClienteServiceImpl(
      ClienteRepository clienteRepository,
      TransacaoRepository transacaoRepository,
      ModelMapper modelMapper) {
    this.clienteRepository = clienteRepository;
    this.transacaoRepository = transacaoRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public ClienteResponseDTO alterar(
      Usuario usuario, long idUsuarioAlterado, ClienteUpsertDTO upsertDto) {
    if (!usuario.getUserId().equals(String.valueOf(idUsuarioAlterado))) {
      throw new CommerceException(ErrorCode.FORBIDDEN);
    }

    Cliente cliente =
        clienteRepository
            .findById(idUsuarioAlterado)
            .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));

    modelMapper.map(upsertDto, cliente);
    clienteRepository.save(cliente);
    return new ClienteResponseDTO(cliente);
  }

  @Override
  public ClienteResponseDTO criar(ClienteUpsertDTO upsertDto) {
    Cliente cliente = modelMapper.map(upsertDto, Cliente.class);

    clienteRepository.save(cliente);
    return new ClienteResponseDTO(cliente);
  }

  @Override
  public void remover(Long id) {
    Cliente cliente =
        clienteRepository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
    clienteRepository.delete(cliente);
  }

  @Override
  public List<ClienteResponseDTO> listarPorNome(String nome) {
    List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
    return clientes.stream().map(ClienteResponseDTO::new).collect(Collectors.toList());
  }

  @Override
  public List<ClienteResponseDTO> listar() {
    List<Cliente> clientes = clienteRepository.findAll();
    return clientes.stream().map(ClienteResponseDTO::new).collect(Collectors.toList());
  }

  @Override
  public ClienteResponseDTO recuperar(Usuario usuario, Long id) {
    var cliente = getCliente(usuario, id);
    return new ClienteResponseDTO(cliente);
  }

  @Override
  public CarteiraResponseDTO recuperarCarteira(Usuario usuario, Long id) {
    var cliente = getCliente(usuario, id);
    return new CarteiraResponseDTO(cliente.getCarteira());
  }

  private Cliente getCliente(Usuario usuario, Long id) {
    if (!usuario.isAdmin() && !usuario.getUserId().equals(String.valueOf(id))) {
      throw new CommerceException(ErrorCode.FORBIDDEN);
    }

    return clienteRepository
        .findById(id)
        .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));
  }

  @Override
  public byte[] gerarExtratoCsv(Usuario usuario, Long clienteId) {
    if (!usuario.isAdmin()) {
      var cliente = (Cliente) usuario;
      if (!clienteId.equals(cliente.getId())) {
        throw new CommerceException(ErrorCode.ACAO_APENAS_ADMIN);
      }
    }

    var extrato = gerarExtrato(usuario, clienteId).collect(Collectors.toList());
    return CsvExporter.gerarCsv(extrato);
  }

  @Override
  public List<ExtratoDTO> listarExtrato(Usuario usuario, ExtratoFiltrosDTO filtros) {
    return gerarExtrato(usuario, filtros);
  }

  private List<ExtratoDTO> gerarExtrato(Usuario usuario, ExtratoFiltrosDTO filtros) {
    Stream<ExtratoDTO> transacoesStream = gerarExtrato(usuario, filtros.getClienteId());

    if (filtros.getDataInicio() != null) {
      transacoesStream =
          transacoesStream.filter(
              t -> t.abertaEm() != null && !t.abertaEm().isBefore(filtros.getDataInicio()));
    }

    if (filtros.getDataFim() != null) {
      transacoesStream =
          transacoesStream.filter(
              t -> t.abertaEm() != null && !t.abertaEm().isAfter(filtros.getDataFim()));
    }

    if (filtros.getTipoAtivo() != null) {
      transacoesStream =
          transacoesStream.filter(
              t -> t.ativo() != null && t.ativo().contains(filtros.getTipoAtivo().name()));
    }

    if (filtros.getTipoOperacao() != null && !filtros.getTipoOperacao().trim().isEmpty()) {
      transacoesStream =
          transacoesStream.filter(
              t -> t.tipo() != null && t.tipo().equalsIgnoreCase(filtros.getTipoOperacao().trim()));
    }

    if (filtros.getNomeAtivo() != null && !filtros.getNomeAtivo().trim().isEmpty()) {
      transacoesStream =
          transacoesStream.filter(
              t ->
                  t.ativo() != null
                      && t.ativo()
                          .toLowerCase()
                          .contains(filtros.getNomeAtivo().trim().toLowerCase()));
    }

    return transacoesStream.collect(Collectors.toList());
  }

  private Stream<ExtratoDTO> gerarExtrato(Usuario usuario, Long filtroClienteId) {
    List<Transacao> transacoes;
    if (usuario.isAdmin() && filtroClienteId == null) {
      transacoes = this.transacaoRepository.findAll();
    } else if (usuario.isAdmin() && filtroClienteId != null) {
      transacoes = this.transacaoRepository.findAllByCliente_Id(filtroClienteId);
    } else {
      var clienteId = ((Cliente) usuario).getId();
      transacoes = this.transacaoRepository.findAllByCliente_Id(clienteId);
    }

    return transacoes.stream().map(ExtratoDTO::fromTransacao);
  }
}

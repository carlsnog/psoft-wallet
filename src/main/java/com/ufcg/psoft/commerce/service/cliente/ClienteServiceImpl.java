package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.CarteiraResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;
import com.ufcg.psoft.commerce.dto.ExtratoDTO;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.mapper.ExtratoMapper;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.util.CsvExporter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ClienteServiceImpl implements ClienteService {

  private final ClienteRepository clienteRepository;
  private final ModelMapper modelMapper;

  public ClienteServiceImpl(ClienteRepository clienteRepository, ModelMapper modelMapper) {
    this.clienteRepository = clienteRepository;
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
  public byte[] gerarExtratoCsv(Long clienteId) {
    Cliente cliente =
        clienteRepository
            .findById(clienteId)
            .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));

    List<ExtratoDTO> linhas = new ArrayList<>();
    cliente.getCompras().forEach(c -> linhas.add(ExtratoMapper.fromCompra(c)));
    cliente.getResgates().forEach(r -> linhas.add(ExtratoMapper.fromResgate(r)));

    return CsvExporter.gerarCsv(linhas);
  }
}

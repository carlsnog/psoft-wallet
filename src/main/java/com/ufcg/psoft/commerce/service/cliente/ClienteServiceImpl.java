package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.service.auth.UsuarioService;
import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final UsuarioService usuarioService;
    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;

    public ClienteServiceImpl(UsuarioService usuarioService, ClienteRepository clienteRepository,
            ModelMapper modelMapper) {
        this.usuarioService = usuarioService;
        this.clienteRepository = clienteRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ClienteResponseDTO alterar(Usuario usuario, long idUsuarioAlterado,
            ClienteUpsertDTO clientePostPutRequestDTO) {
        if (usuario.getId() != idUsuarioAlterado) {
            throw new CommerceException(ErrorCode.FORBIDDEN);
        }

        Cliente cliente = clienteRepository.findById(idUsuarioAlterado)
                .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_EXISTE));

        modelMapper.map(clientePostPutRequestDTO, cliente);
        clienteRepository.save(cliente);
        return new ClienteResponseDTO(cliente);
    }

    @Override
    public ClienteResponseDTO criar(ClienteUpsertDTO clientePostPutRequestDTO) {
        Cliente cliente = modelMapper.map(clientePostPutRequestDTO, Cliente.class);

        if (!usuarioService.isCodigoValido(cliente.getCodigoAcesso())) {
            throw new CommerceException(ErrorCode.COD_ACESSO_INVALIDO);
        }

        clienteRepository.save(cliente);
        return new ClienteResponseDTO(cliente);
    }

    @Override
    public void remover(Long id, String codigoAcesso) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_EXISTE));
        clienteRepository.delete(cliente);
    }

    @Override
    public List<ClienteResponseDTO> listarPorNome(String nome) {
        List<Cliente> clientes = clienteRepository.findByNomeContainingIgnoreCase(nome);
        return clientes.stream()
                .map(ClienteResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClienteResponseDTO> listar() {
        List<Cliente> clientes = clienteRepository.findAll();
        return clientes.stream()
                .map(ClienteResponseDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public ClienteResponseDTO recuperar(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_EXISTE));
        return new ClienteResponseDTO(cliente);
    }
}


package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
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

        clienteRepository.save(cliente);
        return new ClienteResponseDTO(cliente);
    }

    @Override
    public void remover(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_EXISTE));
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
        if (!usuario.isAdmin() && usuario.getId() != id) {
            throw new CommerceException(ErrorCode.FORBIDDEN);
        }

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_EXISTE));
        return new ClienteResponseDTO(cliente);
    }
}

package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;

import java.util.List;

public interface ClienteService {

    ClienteResponseDTO alterar(Long id, String codigoAcesso, ClienteUpsertDTO clientePostPutRequestDTO);

    List<ClienteResponseDTO> listar();

    ClienteResponseDTO recuperar(Long id);

    ClienteResponseDTO criar(ClienteUpsertDTO clientePostPutRequestDTO);

    void remover(Long id, String codigoAcesso);

    List<ClienteResponseDTO> listarPorNome(String nome);
}

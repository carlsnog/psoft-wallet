package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;
import com.ufcg.psoft.commerce.model.Usuario;
import java.util.List;

public interface ClienteService {
  ClienteResponseDTO alterar(Usuario usuario, long idUsuarioAlterado, ClienteUpsertDTO upsertDto);

  List<ClienteResponseDTO> listar();

  ClienteResponseDTO recuperar(Usuario usuario, Long id);

  ClienteResponseDTO criar(ClienteUpsertDTO upsertDto);

  void remover(Long id);

  List<ClienteResponseDTO> listarPorNome(String nome);
}

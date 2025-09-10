package com.ufcg.psoft.commerce.service.cliente;

import com.ufcg.psoft.commerce.dto.CarteiraResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteResponseDTO;
import com.ufcg.psoft.commerce.dto.ClienteUpsertDTO;
import com.ufcg.psoft.commerce.dto.ExtratoDTO;
import com.ufcg.psoft.commerce.dto.ExtratoFiltrosDTO;
import com.ufcg.psoft.commerce.model.Usuario;
import java.util.List;

public interface ClienteService {
  ClienteResponseDTO alterar(Usuario usuario, long idUsuarioAlterado, ClienteUpsertDTO upsertDto);

  List<ClienteResponseDTO> listar();

  ClienteResponseDTO recuperar(Usuario usuario, Long id);

  CarteiraResponseDTO recuperarCarteira(Usuario usuario, Long id);

  ClienteResponseDTO criar(ClienteUpsertDTO upsertDto);

  void remover(Long id);

  List<ClienteResponseDTO> listarPorNome(String nome);

  byte[] gerarExtratoCsv(Usuario usuario, Long clienteId);

  List<ExtratoDTO> listarExtrato(Usuario usuario, ExtratoFiltrosDTO filtros);
}

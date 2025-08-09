package com.ufcg.psoft.commerce.service.interesse;

import com.ufcg.psoft.commerce.dto.InteresseCreateDTO;
import com.ufcg.psoft.commerce.dto.InteresseResponseDTO;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.auth.UsuarioService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InteresseServiceImpl implements InteresseService {

  @Autowired private InteresseRepository interesseRepository;
  @Autowired private ClienteRepository clienteRepository;
  @Autowired private UsuarioService usuarioService;
  @Autowired private AtivoRepository ativoRepository;

  @Override
  public InteresseResponseDTO criarInteresseCotacao(
      Usuario usuario, InteresseCreateDTO interesseDto) {
    if (!podeCriarClienteInteresse(usuario, interesseDto.getClienteId())) {
      throw new CommerceException(ErrorCode.FORBIDDEN);
    }

    Cliente cliente =
        clienteRepository
            .findById(interesseDto.getClienteId())
            .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));

    Ativo ativo =
        ativoRepository
            .findById(interesseDto.getAtivoId())
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

    if (!usuarioService.podeVerTipoAtivo(usuario, ativo.getTipo())) {
      throw new CommerceException(ErrorCode.FORBIDDEN);
    }

    if (ativo.getStatus() != StatusAtivo.DISPONIVEL) {
      throw new CommerceException(ErrorCode.INTERESSE_COTACAO_ATIVO_NAO_DISPONIVEL);
    }

    Interesse interesse =
        interesseRepository.save(
            Interesse.builder()
                .tipo(TipoInteresseEnum.COTACAO)
                .cliente(cliente)
                .ativo(ativo)
                .build());

    return new InteresseResponseDTO(interesse);
  }

  @Override
  public InteresseResponseDTO criarInteresseDisponibilidade(
      Usuario usuario, InteresseCreateDTO interesseDto) {
    if (!podeCriarClienteInteresse(usuario, interesseDto.getClienteId())) {
      throw new CommerceException(ErrorCode.FORBIDDEN);
    }

    Cliente cliente =
        clienteRepository
            .findById(interesseDto.getClienteId())
            .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_ENCONTRADO));

    Ativo ativo =
        ativoRepository
            .findById(interesseDto.getAtivoId())
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

    if (!usuarioService.podeVerTipoAtivo(usuario, ativo.getTipo())) {
      throw new CommerceException(ErrorCode.FORBIDDEN);
    }

    if (ativo.getStatus() != StatusAtivo.INDISPONIVEL) {
      throw new CommerceException(ErrorCode.INTERESSE_DISPONIBILIDADE_ATIVO_JA_DISPONIVEL);
    }

    Interesse interesse =
        interesseRepository.save(
            Interesse.builder()
                .tipo(TipoInteresseEnum.DISPONIBILIDADE)
                .cliente(cliente)
                .ativo(ativo)
                .build());

    return new InteresseResponseDTO(interesse);
  }

  private boolean podeCriarClienteInteresse(Usuario usuario, long clienteId) {
    if (usuario.isAdmin()) return true;
    var cliente = (Cliente) usuario;
    return cliente.getId() == clienteId;
  }

  @Override
  public void remover(Long id, Usuario usuario) {
    var interesse = interesseRepository.findById(id).orElse(null);
    if (interesse == null || !podeCriarClienteInteresse(usuario, interesse.getClienteId())) {
      throw new CommerceException(ErrorCode.INTERESSE_NAO_ENCONTRADO);
    }

    interesseRepository.delete(interesse);
  }

  @Override
  public InteresseResponseDTO buscarPorId(Long id, Usuario usuario) {
    Interesse interesse =
        interesseRepository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.INTERESSE_NAO_ENCONTRADO));

    if (interesse == null || !podeCriarClienteInteresse(usuario, interesse.getClienteId())) {
      throw new CommerceException(ErrorCode.INTERESSE_NAO_ENCONTRADO);
    }

    return new InteresseResponseDTO(interesse);
  }

  @Override
  public List<InteresseResponseDTO> listar() {
    List<Interesse> interesses = interesseRepository.findAll();
    return interesses.stream().map(InteresseResponseDTO::new).collect(Collectors.toList());
  }
}

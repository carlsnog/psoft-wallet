package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoCreateDTO;
import com.ufcg.psoft.commerce.dto.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.AtivoUpdateDTO;
import com.ufcg.psoft.commerce.dto.ValorUpsertDTO;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.service.auth.UsuarioService;
import com.ufcg.psoft.commerce.service.interesse.listeners.disponivel.AtivoDisponivelEvent;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class AtivoServiceImpl implements AtivoService {

  @Autowired private AtivoRepository repository;
  @Autowired private AtivoFactory ativoFactory;
  @Autowired private ApplicationEventPublisher eventPublisher;
  @Autowired private UsuarioService usuarioService;
  @Autowired ModelMapper modelMapper;

  @Override
  public AtivoResponseDTO criar(AtivoCreateDTO dto) {
    if (repository.existsByNome(dto.getNome())) {
      throw new CommerceException(ErrorCode.ATIVO_JA_EXISTE);
    }

    Ativo ativo = ativoFactory.criarAtivo(dto);
    repository.save(ativo);
    return modelMapper.map(ativo, AtivoResponseDTO.class);
  }

  @Override
  public AtivoResponseDTO atualizar(Long id, AtivoUpdateDTO dto) {
    Ativo ativo =
        repository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

    if (repository.existsByNomeAndIdNot(dto.getNome(), id)) {
      throw new CommerceException(ErrorCode.ATIVO_JA_EXISTE);
    }

    modelMapper.map(dto, ativo);
    repository.save(ativo);
    return modelMapper.map(ativo, AtivoResponseDTO.class);
  }

  @Override
  public AtivoResponseDTO atualizarCotacao(Long id, ValorUpsertDTO dto) {
    Ativo ativo =
        repository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

    ativo.atualizarValor(dto.getValor());
    repository.save(ativo);
    return modelMapper.map(ativo, AtivoResponseDTO.class);
  }

  @Override
  public void remover(Long id) {
    Ativo ativo =
        repository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));
    repository.delete(ativo);
  }

  @Override
  public AtivoResponseDTO recuperar(Long id, Usuario usuario) {
    Ativo ativo =
        repository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

    if (!usuarioService.podeVerTipoAtivo(usuario, ativo.getTipo())) {
      throw new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO);
    }

    return modelMapper.map(ativo, AtivoResponseDTO.class);
  }

  @Override
  public List<AtivoResponseDTO> listar(Usuario usuario) {
    List<Ativo> ativos = repository.findAll();

    return ativos.stream()
        .filter(ativo -> usuarioService.podeVerTipoAtivo(usuario, ativo.getTipo()))
        .map(AtivoResponseDTO::new)
        .collect(Collectors.toList());
  }

  @Override
  public AtivoResponseDTO alterarStatus(Long id, StatusAtivo novoStatus) {
    Ativo ativo =
        repository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

    if (ativo.getStatus() == novoStatus) {
      throw new CommerceException(ErrorCode.ATIVO_JA_ESTA_NO_STATUS);
    }

    ativo.setStatus(novoStatus);
    repository.save(ativo);

    if (novoStatus == StatusAtivo.DISPONIVEL) {
      eventPublisher.publishEvent(new AtivoDisponivelEvent(ativo));
    }

    return new AtivoResponseDTO(ativo);
  }
}

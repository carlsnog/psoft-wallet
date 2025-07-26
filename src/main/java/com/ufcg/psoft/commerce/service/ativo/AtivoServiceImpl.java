package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.AtivoUpsertDTO;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AtivoServiceImpl implements AtivoService {

  @Autowired private AtivoRepository repository;

  @Autowired private AtivoFactory ativoFactory;

  @Autowired ModelMapper modelMapper;

  @Override
  public AtivoResponseDTO criar(AtivoUpsertDTO dto) {
    Ativo ativo = ativoFactory.criarAtivo(dto);
    repository.save(ativo);
    return modelMapper.map(ativo, AtivoResponseDTO.class);
  }

  @Override
  public AtivoResponseDTO atualizar(Long id, AtivoUpsertDTO dto) {
    Ativo ativo =
        repository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

    if (!ativo.getTipo().equals(dto.getTipo())) {
      throw new CommerceException(ErrorCode.ALTERACAO_TIPO_NAO_PERMITIDA);
    }

    modelMapper.map(dto, ativo);
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
  public AtivoResponseDTO buscarPorId(Long id) {
    Ativo ativo =
        repository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));
    return modelMapper.map(ativo, AtivoResponseDTO.class);
  }

  @Override
  public List<AtivoResponseDTO> listarTodos() {
    List<Ativo> ativos = repository.findAll();
    return ativos.stream().map(AtivoResponseDTO::new).collect(Collectors.toList());
  }
}

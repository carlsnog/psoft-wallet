package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoCreateDTO;
import com.ufcg.psoft.commerce.dto.AtivoResponseDTO;
import com.ufcg.psoft.commerce.dto.AtivoUpdateDTO;
import com.ufcg.psoft.commerce.dto.ValorUpsertDTO;
import com.ufcg.psoft.commerce.enums.AtivoTipo;
import com.ufcg.psoft.commerce.enums.PlanoEnum;
import com.ufcg.psoft.commerce.enums.StatusAtivo;
import com.ufcg.psoft.commerce.enums.TipoInteresseEnum;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import java.util.List;
import java.util.stream.Collectors;

import com.ufcg.psoft.commerce.repository.InteresseRepository;
import com.ufcg.psoft.commerce.service.interesse.NotificacaoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AtivoServiceImpl implements AtivoService {

  @Autowired private AtivoRepository repository;

  @Autowired private AtivoFactory ativoFactory;

  @Autowired private NotificacaoService notificacaoService;

  @Autowired private InteresseRepository interesseRepository;

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
  public AtivoResponseDTO buscarPorId(Long id) {
    Ativo ativo =
        repository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));
    return modelMapper.map(ativo, AtivoResponseDTO.class);
  }

  @Override
  public List<AtivoResponseDTO> listar(Usuario usuario) {
    List<Ativo> ativos;

    if (podeVerTodosAtivos(usuario)) {
      ativos = repository.findAll();
    } else {
      ativos = repository.findAllByTipo(AtivoTipo.TESOURO);
    }

    return ativos.stream().map(AtivoResponseDTO::new).collect(Collectors.toList());
  }

  private boolean podeVerTodosAtivos(Usuario usuario) {
    if (usuario.isAdmin()) {
      return true;
    }

    var cliente = (Cliente) usuario;
    if (cliente.getPlano().equals(PlanoEnum.PREMIUM)) {
      return true;
    }

    return false;
  }

  @Override
  public AtivoResponseDTO alterarStatus(Long id, StatusAtivo novoStatus) {
    Ativo ativo =
        repository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));
    ativo.setStatus(novoStatus);
    repository.save(ativo);

    if(novoStatus == StatusAtivo.DISPONIVEL) {
      notificaInteressadosPorDisponibilidade(ativo);
    }
    return new AtivoResponseDTO(ativo);
  }

  private void notificaInteressadosPorDisponibilidade(Ativo ativo) {
    List<Interesse> interesses = buscarInteressesPorDisponibilidade(ativo);
    if(!interesses.isEmpty()) {
      notificacaoService.notificarDisponibilidade(ativo);
      interesseRepository.deleteAll(interesses);
    }
  }

  private List<Interesse> buscarInteressesPorDisponibilidade(Ativo ativo) {
    return interesseRepository.findByTipoAndAtivo_Id(TipoInteresseEnum.DISPONIBILIDADE, ativo.getId());
  }
}

package com.ufcg.psoft.commerce.service.interesse;

import com.ufcg.psoft.commerce.dto.InteresseCreateDTO;
import com.ufcg.psoft.commerce.dto.InteresseResponseDTO;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.exception.ErrorCode;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.model.Cliente;
import com.ufcg.psoft.commerce.model.Interesse;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import com.ufcg.psoft.commerce.repository.ClienteRepository;
import com.ufcg.psoft.commerce.repository.InteresseRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InteresseServiceImpl implements InteresseService {

  @Autowired private InteresseRepository interesseRepository;

  @Autowired private ClienteRepository clienteRepository;

  @Autowired private AtivoRepository ativoRepository;

  @Override
  public InteresseResponseDTO criar(InteresseCreateDTO interesseDto) {
    Cliente cliente =
        clienteRepository
            .findById(interesseDto.getClienteId())
            .orElseThrow(() -> new CommerceException(ErrorCode.CLIENTE_NAO_EXISTE));

    Ativo ativo =
        ativoRepository
            .findById(interesseDto.getAtivoId())
            .orElseThrow(() -> new CommerceException(ErrorCode.ATIVO_NAO_ENCONTRADO));

    Interesse interesse =
        Interesse.builder().tipo(interesseDto.getTipo()).cliente(cliente).ativo(ativo).build();

    Interesse savedInteresse = interesseRepository.save(interesse);

    return new InteresseResponseDTO(savedInteresse);
  }

  @Override
  public void remover(Long id) {
    if (!interesseRepository.existsById(id)) {
      throw new CommerceException(ErrorCode.INTERESSE_NAO_ENCONTRADO);
    }
    interesseRepository.deleteById(id);
  }

  @Override
  public InteresseResponseDTO buscarPorId(Long id) {
    Interesse interesse =
        interesseRepository
            .findById(id)
            .orElseThrow(() -> new CommerceException(ErrorCode.INTERESSE_NAO_ENCONTRADO));

    return new InteresseResponseDTO(interesse);
  }

  @Override
  public List<InteresseResponseDTO> listar(Usuario usuario) {
    List<Interesse> interesses = interesseRepository.findAll();

    return interesses.stream()
        .map(InteresseResponseDTO::new)
        .collect(Collectors.toList());
  }
}

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
import com.ufcg.psoft.commerce.service.interesse.validadores.InteresseValidador;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InteresseServiceImpl implements InteresseService {

  @Autowired private InteresseRepository interesseRepository;

  @Autowired private ClienteRepository clienteRepository;

  @Autowired private AtivoRepository ativoRepository;

  @Autowired private InteresseValidador interesseValidador;

  @Override
  public InteresseResponseDTO criar(InteresseCreateDTO interesseDto, Usuario usuario) {
    if (!isAutorizado(usuario, interesseDto.getClienteId())) {
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

    interesseValidador.validar(usuario, ativo, interesseDto.getTipo());

    Interesse interesse =
        interesseRepository.save(
            Interesse.builder().tipo(interesseDto.getTipo()).cliente(cliente).ativo(ativo).build());

    return new InteresseResponseDTO(interesse);
  }

  @Override
  public void remover(Long id, Usuario usuario) {
    var interesse = interesseRepository.findById(id).orElse(null);
    if (interesse == null || !isAutorizado(usuario, interesse.getClienteId())) {
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

    if (!isAutorizado(usuario, interesse.getClienteId())) {
      throw new CommerceException(ErrorCode.INTERESSE_NAO_ENCONTRADO);
    }

    return new InteresseResponseDTO(interesse);
  }

  @Override
  public List<InteresseResponseDTO> listar() {
    List<Interesse> interesses = interesseRepository.findAll();
    return interesses.stream().map(InteresseResponseDTO::new).collect(Collectors.toList());
  }

  private boolean isAutorizado(Usuario usuario, long clienteId) {
    if (usuario.isAdmin()) return true;
    var cliente = (Cliente) usuario;
    return cliente.getId() == clienteId;
  }
}

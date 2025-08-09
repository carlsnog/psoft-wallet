package com.ufcg.psoft.commerce.controller;

import com.ufcg.psoft.commerce.dto.InteresseCreateDTO;
import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.auth.Autenticado;
import com.ufcg.psoft.commerce.http.request.RequestUser;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.service.interesse.InteresseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/interesses")
@Autenticado()
public class InteresseController {

  @Autowired InteresseService interesseService;

  @Autenticado(TipoAutenticacao.PREMIUM)
  @PostMapping("/preco")
  public ResponseEntity<?> criarInteressePreco(
      @RequestBody @Valid InteresseCreateDTO interesseDto, @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(interesseService.criarInteressePreco(usuario, interesseDto));
  }

  @PostMapping("/disponibilidade")
  public ResponseEntity<?> criarInteresseDisponibilidade(
      @RequestBody @Valid InteresseCreateDTO interesseDto, @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(interesseService.criarInteresseDisponibilidade(usuario, interesseDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> excluirInteresse(@PathVariable Long id, @RequestUser Usuario usuario) {
    interesseService.remover(id, usuario);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> recuperarInteresse(@PathVariable Long id, @RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.OK).body(interesseService.buscarPorId(id, usuario));
  }

  @GetMapping("")
  @Autenticado(TipoAutenticacao.ADMIN)
  public ResponseEntity<?> listarInteresses() {
    return ResponseEntity.status(HttpStatus.OK).body(interesseService.listar());
  }
}

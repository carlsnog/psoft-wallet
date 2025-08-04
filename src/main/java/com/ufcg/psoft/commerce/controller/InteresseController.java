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
@Autenticado(TipoAutenticacao.ADMIN)
public class InteresseController {

  @Autowired InteresseService interesseService;

  @PostMapping
  public ResponseEntity<?> criarInteresse(@RequestBody @Valid InteresseCreateDTO interesseDto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(interesseService.criar(interesseDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> excluirInteresse(@PathVariable Long id) {
    interesseService.remover(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> recuperarInteresse(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK).body(interesseService.buscarPorId(id));
  }

  @GetMapping("")
  @Autenticado(TipoAutenticacao.NORMAL)
  public ResponseEntity<?> listarInteresses(@RequestUser Usuario usuario) {
    return ResponseEntity.status(HttpStatus.OK).body(interesseService.listar(usuario));
  }
}

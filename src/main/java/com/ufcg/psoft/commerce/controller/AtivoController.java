package com.ufcg.psoft.commerce.controller;


import com.ufcg.psoft.commerce.dto.AtivoPostPutRequestDTO;
import com.ufcg.psoft.commerce.service.ativo.AtivoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/ativos",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class AtivoController {

    @Autowired
    AtivoService ativoService;

    @PostMapping
    public ResponseEntity<?> criarAtivo(
            @RequestBody @Valid AtivoPostPutRequestDTO ativoDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ativoService.criar(ativoDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarAtivo(
            @PathVariable Long id,
            @RequestBody @Valid AtivoPostPutRequestDTO ativoDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.atualizar(id, ativoDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluirAtivo(
            @PathVariable Long id) {
        ativoService.remover(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body("");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> recuperarAtivo(
            @PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.buscarPorId(id));
    }

    @GetMapping("")
    public ResponseEntity<?> listarAtivos() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ativoService.listarTodos());
    }
}

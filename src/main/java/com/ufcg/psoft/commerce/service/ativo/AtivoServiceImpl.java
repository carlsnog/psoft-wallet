package com.ufcg.psoft.commerce.service.ativo;

import com.ufcg.psoft.commerce.dto.AtivoUpsertDTO;
import com.ufcg.psoft.commerce.dto.AtivoResponseDTO;
import com.ufcg.psoft.commerce.model.Ativo;
import com.ufcg.psoft.commerce.repository.AtivoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AtivoServiceImpl implements AtivoService{

    @Autowired
    private AtivoRepository repository;

    @Autowired
    private AtivoFactoryService ativoFactory;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public AtivoResponseDTO criar(AtivoUpsertDTO dto) {
        Ativo ativo = ativoFactory.criarAtivo(dto);
        repository.save(ativo);
        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public AtivoResponseDTO atualizar(Long id, @org.jetbrains.annotations.NotNull AtivoUpsertDTO dto) {
        Ativo ativo = repository.findById(id).orElseThrow(() -> new RuntimeException("Ativo não encontrado"));

        String tipoAtual = ativo.getTipo().toUpperCase();
        if (!tipoAtual.equals(dto.getTipo().toUpperCase())) {
            throw new IllegalArgumentException("Não é permitido alterar o tipo do ativo.");
        }

        modelMapper.map(dto, ativo);
        repository.save(ativo);
        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public void remover(Long id) {
        Ativo ativo = repository.findById(id).orElseThrow(() -> new RuntimeException("Ativo não encontrado"));
        repository.delete(ativo);
    }

    @Override
    public AtivoResponseDTO buscarPorId(Long id) {
        Ativo ativo = repository.findById(id).orElseThrow(() -> new RuntimeException("Ativo não encontrado"));
        return modelMapper.map(ativo, AtivoResponseDTO.class);
    }

    @Override
    public List<AtivoResponseDTO> listarTodos() {
        List<Ativo> ativos = repository.findAll();
        return ativos.stream()
                .map(AtivoResponseDTO::new)
                .collect(Collectors.toList());
    }


}

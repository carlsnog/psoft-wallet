package com.ufcg.psoft.commerce.service.extrato;

import com.ufcg.psoft.commerce.dto.CompraFilterDTO;
import com.ufcg.psoft.commerce.dto.OperacaoFilterDTO;
import com.ufcg.psoft.commerce.dto.OperacaoResponseDTO;
import com.ufcg.psoft.commerce.dto.ResgateFilterDTO;
import com.ufcg.psoft.commerce.enums.TipoTransacao;
import com.ufcg.psoft.commerce.model.transacao.compra.Compra;
import com.ufcg.psoft.commerce.model.transacao.resgate.Resgate;
import com.ufcg.psoft.commerce.repository.CompraRepository;
import com.ufcg.psoft.commerce.repository.ResgateRepository;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminExtratoServiceImpl implements AdminExtratoService {
  private final CompraRepository compraRepository;
  private final ResgateRepository resgateRepository;

  public AdminExtratoServiceImpl(
      CompraRepository compraRepository, ResgateRepository resgateRepository) {
    this.compraRepository = compraRepository;
    this.resgateRepository = resgateRepository;
  }

  @Override
  public Page<OperacaoResponseDTO> buscarOperacoes(OperacaoFilterDTO filter, Pageable pageable) {
    TipoTransacao tipo =
        (filter == null || filter.getTipoTransacao() == null)
            ? TipoTransacao.ALL
            : filter.getTipoTransacao();

    boolean includeCompra = tipo == TipoTransacao.ALL || tipo == TipoTransacao.COMPRA;
    boolean includeResgate = tipo == TipoTransacao.ALL || tipo == TipoTransacao.RESGATE;
    List<OperacaoResponseDTO> combined = new ArrayList<>();

    if (includeCompra) {
      CompraFilterDTO cf = new CompraFilterDTO();
      cf.setAtivoId(filter.getAtivoId());
      cf.setStartDate(filter.getStartDate());
      cf.setEndDate(filter.getEndDate());
      // status mapping omitted - admin can filter by status if wanted
      Page<Compra> comprasPage =
          compraRepository.findAll(CompraSpecifications.byFilter(cf), Pageable.unpaged());
      combined.addAll(
          comprasPage.stream()
              .map(
                  c -> {
                    OperacaoResponseDTO dto = new OperacaoResponseDTO();
                    dto.setId(c.getId());
                    dto.setTipoTransacao(TipoTransacao.COMPRA);
                    dto.setClienteId(c.getCliente().getId());
                    dto.setAtivoId(c.getAtivo().getId());
                    dto.setAtivoNome(c.getAtivo().getNome());
                    dto.setValorTotal(
                        c.getValorUnitario().multiply(BigDecimal.valueOf(c.getQuantidade())));
                    dto.setStatus(c.getStatus().name());
                    dto.setInicio(c.getAbertaEm());
                    dto.setFim(c.getFinalizadaEm());
                    return dto;
                  })
              .collect(Collectors.toList()));
    }

    if (includeResgate) {
      ResgateFilterDTO rf = new ResgateFilterDTO();
      rf.setAtivoId(filter.getAtivoId());
      rf.setStartDate(filter.getStartDate());
      rf.setEndDate(filter.getEndDate());
      Page<Resgate> resgatesPage =
          resgateRepository.findAll(ResgateSpecifications.byFilter(rf), Pageable.unpaged());
      combined.addAll(
          resgatesPage.stream()
              .map(
                  r -> {
                    OperacaoResponseDTO dto = new OperacaoResponseDTO();
                    dto.setId(r.getId());
                    dto.setTipoTransacao(TipoTransacao.RESGATE);
                    dto.setClienteId(r.getCliente().getId());
                    dto.setAtivoId(r.getAtivo().getId());
                    dto.setAtivoNome(r.getAtivo().getNome());
                    dto.setValorTotal(r.getValorTotal());
                    dto.setStatus(r.getStatus().name());
                    dto.setInicio(r.getAbertaEm());
                    dto.setFim(r.getFinalizadaEm());
                    return dto;
                  })
              .collect(Collectors.toList()));
    }

    // filtrar por clienteId/ativoTipo (se fornecidos)
    Stream<OperacaoResponseDTO> stream = combined.stream();
    if (filter.getClienteId() != null) {
      stream = stream.filter(o -> Objects.equals(o.getClienteId(), filter.getClienteId()));
    }
    if (filter.getAtivoTipo() != null) {
      stream =
          stream.filter(
              o -> {
                // Caso precise mapear ativoId -> ativoTipo seria melhor juntar com entidade Ativo;
                // aqui suponho que
                // você pode estender o DTO com ativoTipo preenchido nas etapas acima.
                return true; // placeholder: adicionar verificação real se ativoTipo preenchido
              });
    }

    List<OperacaoResponseDTO> sorted =
        stream
            .sorted(Comparator.comparing(OperacaoResponseDTO::getInicio).reversed())
            .collect(Collectors.toList());

    // paginação in-memory
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), sorted.size());
    List<OperacaoResponseDTO> pageContent =
        start <= end ? sorted.subList(start, end) : Collections.emptyList();
    return new PageImpl<>(pageContent, pageable, sorted.size());
  }
}

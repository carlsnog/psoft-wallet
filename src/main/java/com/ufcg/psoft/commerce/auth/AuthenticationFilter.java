package com.ufcg.psoft.commerce.auth;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.commerce.auth.autenticador.AutenticadorFactory;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.http.request.CachedBodyRequest;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    public static final String USER_ATTRIBUTE = "usuario";

    private final RequestMappingHandlerMapping handlerMapping;
    private final ObjectMapper mapper;
    private final AutenticadorFactory autenticadorFactory;

    public AuthenticationFilter(
            AutenticadorFactory autenticadorFactory,
            ObjectMapper mapper,
            RequestMappingHandlerMapping handlerMapping) {
        this.autenticadorFactory = autenticadorFactory;
        this.mapper = mapper;
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        var cachedBodyRequest = new CachedBodyRequest(request);

        var handler = getHandler(request);
        if (handler == null) {
            filterChain.doFilter(cachedBodyRequest, response);
            return;
        }

        var codigoAcesso = getCodigoAcesso(cachedBodyRequest);

        var tipo = getTipoAutenticacao(handler);
        var autenticador = autenticadorFactory.getAutenticador(tipo);

        try {
            var usuario = autenticador.autenticar(codigoAcesso);

            request.setAttribute(USER_ATTRIBUTE, usuario);

            filterChain.doFilter(cachedBodyRequest, response);

        } catch (CommerceException e) {
            response.setStatus(e.getStatus().value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(this.mapper.writeValueAsString(e.getErrorDTO()));
            response.getWriter().flush();
        }
    }

    private String getCodigoAcesso(CachedBodyRequest request) {
        try {
            var dto = mapper.readValue(request.getInputStream(), CodigoAcessoDto.class);
            return dto.getCodigoAcesso();
        } catch (Exception e) {
            return "";
        }
    }

    private HandlerExecutionChain getHandler(HttpServletRequest request) {
        try {
            return handlerMapping.getHandler(request);
        } catch (Exception e) {
            return null;
        }
    }

    private TipoAutenticacao getTipoAutenticacao(HandlerExecutionChain handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler.getHandler();
        Autenticado tipo = handlerMethod.getMethodAnnotation(Autenticado.class);

        if (tipo == null) {
            tipo = handlerMethod.getBeanType().getAnnotation(Autenticado.class);
        }

        if (tipo == null) {
            return TipoAutenticacao.PUBLICA;
        }

        return tipo.value();
    }
}
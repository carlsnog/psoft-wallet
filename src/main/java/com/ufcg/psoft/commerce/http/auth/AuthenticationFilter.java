package com.ufcg.psoft.commerce.http.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.exception.CommerceException;
import com.ufcg.psoft.commerce.service.auth.UsuarioService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

  public static final String ATRIBUTO_USUARIO = "usuario";
  public static final String USUARIO_PARAM = "userId";
  public static final String COD_ACESSO_PARAM = "codigoAcesso";

  private final RequestMappingHandlerMapping handlerMapping;
  private final ObjectMapper mapper;
  private final UsuarioService userService;

  public AuthenticationFilter(
      UsuarioService userService,
      ObjectMapper mapper,
      RequestMappingHandlerMapping handlerMapping) {
    this.userService = userService;
    this.mapper = mapper;
    this.handlerMapping = handlerMapping;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    var handler = getHandler(request);
    if (handler == null) {
      filterChain.doFilter(request, response);
      return;
    }

    var auth = getAuthorization(request);
    var tipo = getTipoAutenticacao(handler);

    try {
      var usuario = userService.getUsuario(auth.getUserId(), auth.getCodigoAcesso(), tipo);

      request.setAttribute(ATRIBUTO_USUARIO, usuario.orElse(null));

      filterChain.doFilter(request, response);
    } catch (CommerceException e) {
      response.setStatus(e.getStatus().value());
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");

      response.getWriter().write(this.mapper.writeValueAsString(e.getErrorDTO()));
      response.getWriter().flush();
    }
  }

  private AuthorizationDTO getAuthorization(HttpServletRequest request) {
    try {
      var authorization = request.getHeader("Authorization");
      if (authorization == null || !authorization.startsWith("Basic ")) {
        return new AuthorizationDTO();
      }

      var authorizationString = authorization.substring(6);
      byte[] decodedBytes = Base64.getDecoder().decode(authorizationString);
      String decodedString = new String(decodedBytes);

      return AuthorizationDTO.from(decodedString);
    } catch (Exception e) {
      return null;
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

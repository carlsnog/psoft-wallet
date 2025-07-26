package com.ufcg.psoft.commerce.http.request;

import com.ufcg.psoft.commerce.http.auth.AuthenticationFilter;
import com.ufcg.psoft.commerce.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RequestUserResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return (parameter.hasParameterAnnotation(RequestUser.class)
        && parameter.getParameterType().equals(Usuario.class));
  }

  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      org.springframework.web.bind.support.WebDataBinderFactory binderFactory)
      throws Exception {
    HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();

    return request.getAttribute(AuthenticationFilter.ATRIBUTO_USUARIO);
  }
}

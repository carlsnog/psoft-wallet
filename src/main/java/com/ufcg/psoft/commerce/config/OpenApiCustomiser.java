package com.ufcg.psoft.commerce.config;

import com.ufcg.psoft.commerce.enums.TipoAutenticacao;
import com.ufcg.psoft.commerce.http.auth.Autenticado;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

/**
 * Customiza o OpenAPI para adicionar a autenticação básica a todas as rotas que precisam de
 * autenticação. A autorização é adicionada automaticamente a todas as rotas que possuem a
 * anotação @Autenticado com tipo diferente de PUBLICA.
 */
@Component
public class OpenApiCustomiser implements OpenApiCustomizer {

  private final RequestMappingInfoHandlerMapping handlerMapping;
  private final SecurityRequirement securityRequirement;

  public OpenApiCustomiser(RequestMappingInfoHandlerMapping handlerMapping) {
    this.handlerMapping = handlerMapping;
    this.securityRequirement = new SecurityRequirement().addList(WebConfig.SECURITY_SCHEME_NAME);
  }

  @Override
  public void customise(OpenAPI openApi) {
    if (openApi.getComponents() == null) {
      openApi.setComponents(new io.swagger.v3.oas.models.Components());
    }

    var handlerMethods = handlerMapping.getHandlerMethods();

    for (var entry : handlerMethods.entrySet()) {
      var mappingInfo = entry.getKey();
      var handlerMethod = entry.getValue();
      var method = handlerMethod.getMethod();

      if (isRotaAutenticada(method)) {
        addSecurityToOperations(openApi, mappingInfo);
      }
    }
  }

  private boolean isRotaAutenticada(Method method) {
    Autenticado anotacao = method.getAnnotation(Autenticado.class);
    if (anotacao == null) {
      anotacao = method.getDeclaringClass().getAnnotation(Autenticado.class);
    }

    if (anotacao == null) {
      return false;
    }

    return anotacao.value() != TipoAutenticacao.PUBLICA;
  }

  private void addSecurityToOperations(OpenAPI openApi, RequestMappingInfo mappingInfo) {
    if (openApi.getPaths() == null) {
      return;
    }

    if (mappingInfo.getPathPatternsCondition() == null) {
      return;
    }

    var patterns = mappingInfo.getPathPatternsCondition().getPatternValues();
    for (var pathPattern : patterns) {
      var pathItem = openApi.getPaths().get(pathPattern);
      if (pathItem != null) {
        addSecurityToPathItem(pathItem, mappingInfo);
      }
    }
  }

  private void addSecurityToPathItem(PathItem pathItem, RequestMappingInfo mappingInfo) {
    if (pathItem == null) {
      return;
    }
    if (mappingInfo.getMethodsCondition() == null) {
      return;
    }

    var methodMapper = getMethodMap(pathItem);

    var methods = mappingInfo.getMethodsCondition().getMethods();
    for (var method : methods) {
      var operation = methodMapper.get(method.name());
      if (operation == null) continue;

      operation.addSecurityItem(securityRequirement);
    }
  }

  private HashMap<String, Operation> getMethodMap(PathItem pathItem) {
    HashMap<String, Operation> methodMapper = new HashMap<>();
    if (pathItem.getGet() != null) methodMapper.put("GET", pathItem.getGet());
    if (pathItem.getPost() != null) methodMapper.put("POST", pathItem.getPost());
    if (pathItem.getPut() != null) methodMapper.put("PUT", pathItem.getPut());
    if (pathItem.getDelete() != null) methodMapper.put("DELETE", pathItem.getDelete());

    return methodMapper;
  }
}

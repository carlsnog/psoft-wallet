package com.ufcg.psoft.commerce.config;

import com.ufcg.psoft.commerce.http.request.RequestUserResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  protected static final String SECURITY_SCHEME_NAME = "basicAuth";

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(new RequestUserResolver());
  }

  /** Configura o OpenAPI para usar autenticação basic */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Especificação da API - P$oft Wallet - Grupo 04")
                .description(
                    "API para gerenciamento de ativos financeiros, gerenciamento de clientes e suas carteiras")
                .version("1.0.0")
                .contact(new Contact().name("Grupo 04"))
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
        .addServersItem(
            new Server().url("http://localhost:8080").description("Servidor de Desenvolvimento"))
        .components(
            new Components()
                .addSecuritySchemes(
                    SECURITY_SCHEME_NAME,
                    new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")));
  }
}

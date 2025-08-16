package com.ufcg.psoft.commerce.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufcg.psoft.commerce.model.Usuario;
import java.util.Base64;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class CustomDriver {
  private final ObjectMapper objectMapper;
  private final MockMvc driver;

  public CustomDriver(MockMvc driver, ObjectMapper objectMapper) {
    this.driver = driver;
    this.objectMapper = objectMapper;
  }

  public CustomDriver(MockMvc driver) {
    this.driver = driver;
    this.objectMapper = new ObjectMapper();
  }

  public String createBasicAuthHeader(String userId, String codigoAcesso) {
    String credentials = userId + ":" + codigoAcesso;
    return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
  }

  public ResultActions get(String url, Usuario usuario) throws Exception {
    return driver.perform(
        MockMvcRequestBuilders.get(url)
            .header(
                "Authorization",
                createBasicAuthHeader(
                    String.valueOf(usuario.getUserId()), usuario.getCodigoAcesso())));
  }

  public ResultActions get(String url) throws Exception {
    return driver.perform(MockMvcRequestBuilders.get(url));
  }

  public ResultActions post(String url, Object body, Usuario usuario) throws Exception {
    return driver.perform(
        MockMvcRequestBuilders.post(url)
            .header(
                "Authorization",
                createBasicAuthHeader(
                    String.valueOf(usuario.getUserId()), usuario.getCodigoAcesso()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)));
  }

  public ResultActions post(String url, Object body) throws Exception {
    return driver.perform(
        MockMvcRequestBuilders.post(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)));
  }

  public ResultActions put(String url, Object body, Usuario usuario) throws Exception {
    return driver.perform(
        MockMvcRequestBuilders.put(url)
            .header(
                "Authorization",
                createBasicAuthHeader(
                    String.valueOf(usuario.getUserId()), usuario.getCodigoAcesso()))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)));
  }

  public ResultActions put(String url, Object body) throws Exception {
    return driver.perform(
        MockMvcRequestBuilders.put(url)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(body)));
  }

  public ResultActions delete(String url, Usuario usuario) throws Exception {
    return driver.perform(
        MockMvcRequestBuilders.delete(url)
            .header(
                "Authorization",
                createBasicAuthHeader(
                    String.valueOf(usuario.getUserId()), usuario.getCodigoAcesso())));
  }

  public ResultActions delete(String url) throws Exception {
    return driver.perform(MockMvcRequestBuilders.delete(url));
  }
}

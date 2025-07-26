
package com.ufcg.psoft.commerce.http.auth;

import com.ufcg.psoft.commerce.service.auth.TipoAutenticacao;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autenticado {
    TipoAutenticacao value() default TipoAutenticacao.NORMAL;
}

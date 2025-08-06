package com.ufcg.psoft.commerce.event;

import com.ufcg.psoft.commerce.model.Ativo;
import org.springframework.context.ApplicationEvent;

public class AtivoDisponivelEvent extends ApplicationEvent {
    private final Ativo ativo;

    public AtivoDisponivelEvent(Object source, Ativo ativo) {
        super(source);
        this.ativo = ativo;
    }

    public Ativo getAtivo() {
        return this.ativo;
    }
}

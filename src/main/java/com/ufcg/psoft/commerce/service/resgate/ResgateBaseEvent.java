package com.ufcg.psoft.commerce.service.resgate;

import com.ufcg.psoft.commerce.model.resgate.Resgate;
import org.springframework.context.ApplicationEvent;

public abstract class ResgateBaseEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    private final transient Resgate resgate;

    public ResgateBaseEvent(Object source, Resgate resgate) {
        super(source);
        this.resgate = resgate;
    }

    public Resgate getResgate() {
        return resgate;
    }
}

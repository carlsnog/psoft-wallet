package com.ufcg.psoft.commerce.service.resgate.listeners;

import com.ufcg.psoft.commerce.model.resgate.Resgate;
import com.ufcg.psoft.commerce.service.resgate.ResgateBaseEvent;

public class ResgateConfirmadoEvent extends ResgateBaseEvent {
    private static final long serialVersionUID = 1L;

    public ResgateConfirmadoEvent(Object source, Resgate resgate) {
        super(source, resgate);
    }
}

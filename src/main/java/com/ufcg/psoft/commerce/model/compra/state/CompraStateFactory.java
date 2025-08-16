package com.ufcg.psoft.commerce.model.compra.state;

import com.ufcg.psoft.commerce.model.compra.Compra;

public class CompraStateFactory {

    public static CompraState getStateInicial(Compra compra) {
        return new SolicitadoState(compra);
    }

    public static CompraState getState(Compra compra) {
        switch (compra.getStatus()) {
            case SOLICITADO:
                return new SolicitadoState(compra);
            case DISPONIVEL:
                return new DisponivelState(compra);
            case COMPRADO:
                return new CompradoState(compra);
            case EM_CARTEIRA:
                return new EmCarteiraState(compra);
            default:
                return null;
        }
    }
}

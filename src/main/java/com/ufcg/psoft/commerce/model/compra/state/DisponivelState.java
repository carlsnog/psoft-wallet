package com.ufcg.psoft.commerce.model.compra.state;

import com.ufcg.psoft.commerce.enums.CompraStatusEnum;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.compra.Compra;

public class DisponivelState extends CompraState {

    public DisponivelState(Compra compra) {
        super(compra);
    }

    @Override
    public void confirmar(Usuario usuario) {
        preValidarAtivo();
        preValidarCliente(usuario);

        setStatus(CompraStatusEnum.COMPRADO);
    }
}

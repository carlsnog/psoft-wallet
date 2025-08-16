package com.ufcg.psoft.commerce.model.compra.state;

import com.ufcg.psoft.commerce.enums.CompraStatusEnum;
import com.ufcg.psoft.commerce.model.Usuario;
import com.ufcg.psoft.commerce.model.compra.Compra;

public class CompradoState extends CompraState {

    public CompradoState(Compra compra) {
        super(compra);
    }

    @Override
    public void confirmar(Usuario usuario) {
        preValidarAtivo();
        preValidarAdmin(usuario);

        setStatus(CompraStatusEnum.EM_CARTEIRA);
    }
}

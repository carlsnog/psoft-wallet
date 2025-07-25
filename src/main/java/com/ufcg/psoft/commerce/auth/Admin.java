package com.ufcg.psoft.commerce.auth;

public class Admin extends Usuario {
    private static Admin instance;

    private static final String CODIGO_ACESSO_ADMIN_PADRAO = "admin@123";
    private static final String CODIGO_ACESSO_ADMIN_ENV = "CODIGO_ACESSO_ADMIN";

    private Admin(String codigoAcesso) {
        super(codigoAcesso);
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    public static Admin getInstance() {
        String codigoAcesso = System.getenv(CODIGO_ACESSO_ADMIN_ENV);
        if (codigoAcesso == null) {
            codigoAcesso = CODIGO_ACESSO_ADMIN_PADRAO;
        }

        if (instance == null) {
            instance = new Admin(codigoAcesso);
        }

        return instance;
    }

}

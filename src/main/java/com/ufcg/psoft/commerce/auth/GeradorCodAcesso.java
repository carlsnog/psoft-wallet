package com.ufcg.psoft.commerce.auth;

import java.security.SecureRandom;
import java.util.List;

public class GeradorCodAcesso {
    private static final SecureRandom random = new SecureRandom();
    private static final int TAMANHO_COD_ACESSO = 10;

    private static final List<Character> CHARSET = List.of(
            // Letras maiúsculas
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            // Letras minúsculas
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            // Dígitos
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            // Símbolos
            '@', '#', '$', '%', '&', '*', '-', '_', '+', '=');

    public static String gerar() {
        StringBuilder sb = new StringBuilder(TAMANHO_COD_ACESSO);
        for (int i = 0; i < TAMANHO_COD_ACESSO; i++) {
            sb.append(CHARSET.get(random.nextInt(CHARSET.size())));
        }
        return sb.toString();
    }
}
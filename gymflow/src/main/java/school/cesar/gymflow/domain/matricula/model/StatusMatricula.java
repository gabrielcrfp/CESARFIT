package school.cesar.gymflow.domain.matricula.model;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

public enum StatusMatricula {
    ATIVA, VENCIDA, CANCELADA;

    public static StatusMatricula de(String texto) {
        if (texto == null || texto.isBlank()) {
            return ATIVA;
        }
        try {
            return StatusMatricula.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException("Situacao de matricula invalida: " + texto);
        }
    }
}

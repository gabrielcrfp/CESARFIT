package school.cesar.gymflow.domain.matricula.model;

import java.time.LocalDate;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Planos oferecidos pela academia. Existem SOMENTE dois:
 * Plano Semanal (vigencia de 7 dias) e Plano Mensal (vigencia de 1 mes).
 * O valor de cada um e definido pelas politicas de preco (Strategy).
 */
public enum Modalidade {
    SEMANAL("Plano Semanal", "7 dias"),
    MENSAL("Plano Mensal", "1 mes");

    private final String nome;
    private final String duracao;

    Modalidade(String nome, String duracao) {
        this.nome = nome;
        this.duracao = duracao;
    }

    public String nome() {
        return nome;
    }

    public String duracao() {
        return duracao;
    }

    /** Data final da vigencia a partir de uma data de inicio. */
    public LocalDate fimAPartirDe(LocalDate inicio) {
        return this == SEMANAL ? inicio.plusDays(7) : inicio.plusMonths(1);
    }

    /** Converte texto vindo da interface; rejeita planos inexistentes (ex.: ANUAL). */
    public static Modalidade de(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new RegraDeNegocioException("Selecione um plano (Semanal ou Mensal)");
        }
        try {
            return Modalidade.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException("Plano invalido: " + texto
                    + ". Os planos disponiveis sao Semanal e Mensal");
        }
    }
}

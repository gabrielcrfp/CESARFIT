package school.cesar.gymflow.domain.matricula.model;

import java.time.LocalDate;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Planos oferecidos pela academia. Existem SOMENTE dois:
 * Plano Mensal (vigencia de 1 mes) e Plano Anual (vigencia de 12 meses).
 * O valor de cada um e definido pelas politicas de preco (Strategy).
 */
public enum Modalidade {
    MENSAL("Plano Mensal", "1 mes", 1),
    ANUAL("Plano Anual", "12 meses", 12);

    private final String nome;
    private final String duracao;
    private final int meses;

    Modalidade(String nome, String duracao, int meses) {
        this.nome = nome;
        this.duracao = duracao;
        this.meses = meses;
    }

    public String nome() {
        return nome;
    }

    public String duracao() {
        return duracao;
    }

    /** Data final da vigencia a partir de uma data de inicio. */
    public LocalDate fimAPartirDe(LocalDate inicio) {
        return inicio.plusMonths(meses);
    }

    public int meses() {
        return meses;
    }

    /** Converte texto vindo da interface; rejeita planos inexistentes (ex.: ANUAL). */
    public static Modalidade de(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new RegraDeNegocioException("Selecione um plano (Mensal ou Anual)");
        }
        try {
            return Modalidade.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException("Plano invalido: " + texto
                    + ". Os planos disponiveis sao Mensal e Anual");
        }
    }
}

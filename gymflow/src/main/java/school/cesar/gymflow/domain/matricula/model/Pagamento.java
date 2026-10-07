package school.cesar.gymflow.domain.matricula.model;

import java.time.LocalDate;

import school.cesar.gymflow.domain.shared.Dinheiro;

/** Entidade interna do agregado Matricula. */
public record Pagamento(Dinheiro valor, LocalDate data, String formaDePagamento,
                        Integer numeroParcela, LocalDate vencimento) {

    public Pagamento(Dinheiro valor, LocalDate data, String formaDePagamento) {
        this(valor, data, formaDePagamento, null, null);
    }

    public static Pagamento novo(Dinheiro valor, String formaDePagamento) {
        return new Pagamento(valor, LocalDate.now(), formaDePagamento);
    }

    public static Pagamento novo(Dinheiro valor, String formaDePagamento,
                                 Integer numeroParcela, LocalDate vencimento) {
        return new Pagamento(valor, LocalDate.now(), formaDePagamento, numeroParcela, vencimento);
    }
}

package school.cesar.gymflow.domain.matricula.model;

import java.time.LocalDate;

import school.cesar.gymflow.domain.shared.Dinheiro;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.shared.Texto;

/** Objeto de valor: um pagamento da matricula. Pode ser estornado uma unica vez. */
public record Pagamento(Dinheiro valor, LocalDate data, String formaDePagamento,
                        boolean estornado, String motivoDoEstorno, LocalDate dataDoEstorno) {

    public Pagamento(Dinheiro valor, LocalDate data, String formaDePagamento) {
        this(valor, data, formaDePagamento, false, null, null);
    }

    public static Pagamento novo(Dinheiro valor, String formaDePagamento) {
        return new Pagamento(valor, LocalDate.now(), formaDePagamento);
    }

    public Pagamento estornar(String motivo) {
        if (estornado) {
            throw new RegraDeNegocioException("Pagamento ja foi estornado");
        }
        String limpo = Texto.obrigatorio(motivo, 10, 300, "Motivo do estorno");
        return new Pagamento(valor, data, formaDePagamento, true, limpo, LocalDate.now());
    }
}

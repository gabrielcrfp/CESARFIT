package school.cesar.gymflow.domain.matricula.politica;

import school.cesar.gymflow.domain.matricula.model.Modalidade;
import school.cesar.gymflow.domain.shared.Dinheiro;

/** Padrao Strategy - estrategia concreta: Plano Mensal = R$ 99,00. */
public class PrecoMensal implements PoliticaDePreco {

    private static final Dinheiro VALOR = Dinheiro.de("99.00");

    @Override
    public Modalidade modalidade() {
        return Modalidade.MENSAL;
    }

    @Override
    public Dinheiro calcular() {
        return VALOR;
    }
}

package school.cesar.gymflow.domain.matricula.politica;

import school.cesar.gymflow.domain.matricula.model.Modalidade;
import school.cesar.gymflow.domain.shared.Dinheiro;

/** Padrao Strategy - estrategia concreta: Plano Anual = R$ 1320,00 (equivale a R$ 110,00 por mes). */
public class PrecoAnual implements PoliticaDePreco {

    private static final Dinheiro VALOR = Dinheiro.de("1320.00");

    @Override
    public Modalidade modalidade() {
        return Modalidade.ANUAL;
    }

    @Override
    public Dinheiro calcular() {
        return VALOR;
    }
}

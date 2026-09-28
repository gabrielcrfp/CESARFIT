package school.cesar.gymflow.domain.matricula.politica;

import school.cesar.gymflow.domain.matricula.model.Modalidade;
import school.cesar.gymflow.domain.shared.Dinheiro;

/** Padrao Strategy - estrategia concreta: Plano Semanal = R$ 50,00. */
public class PrecoSemanal implements PoliticaDePreco {

    private static final Dinheiro VALOR = Dinheiro.de("50.00");

    @Override
    public Modalidade modalidade() {
        return Modalidade.SEMANAL;
    }

    @Override
    public Dinheiro calcular() {
        return VALOR;
    }
}

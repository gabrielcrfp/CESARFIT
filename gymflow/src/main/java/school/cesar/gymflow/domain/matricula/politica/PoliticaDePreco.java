package school.cesar.gymflow.domain.matricula.politica;

import school.cesar.gymflow.domain.matricula.model.Modalidade;
import school.cesar.gymflow.domain.shared.Dinheiro;

/** Padrao Strategy - contrato de calculo do valor de um plano. */
public interface PoliticaDePreco {

    Modalidade modalidade();

    Dinheiro calcular();
}

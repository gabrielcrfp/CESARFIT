package school.cesar.gymflow.domain.treino.servico;

import school.cesar.gymflow.domain.matricula.model.Objetivo;

/** Seleciona a implementacao do Template Method conforme o objetivo do aluno. */
public final class FabricaDeGerador {

    private FabricaDeGerador() {
    }

    public static GeradorDeFicha para(Objetivo objetivo) {
        return switch (objetivo) {
            case HIPERTROFIA -> new GeradorHipertrofia();
            case EMAGRECIMENTO -> new GeradorEmagrecimento();
            case CONDICIONAMENTO -> new GeradorCondicionamento();
        };
    }
}

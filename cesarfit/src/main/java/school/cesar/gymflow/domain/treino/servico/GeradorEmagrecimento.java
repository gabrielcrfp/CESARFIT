package school.cesar.gymflow.domain.treino.servico;

import java.util.List;

import school.cesar.gymflow.domain.treino.model.GrupoMuscular;

/** Emagrecimento: cardio e circuitos com descanso curto. */
public class GeradorEmagrecimento extends GeradorDeFicha {

    @Override
    protected List<Vaga> vagas() {
        return List.of(
                new Vaga("A", GrupoMuscular.CARDIO, "Esteira em ritmo moderado", 1, 30, 0),
                new Vaga("A", GrupoMuscular.PERNAS, "Circuito de agachamento", 3, 15, 45),
                new Vaga("A", GrupoMuscular.PERNAS, "Afundo com halteres", 3, 12, 45),
                new Vaga("B", GrupoMuscular.CARDIO, "Bicicleta ergometrica", 1, 20, 0),
                new Vaga("B", GrupoMuscular.COSTAS, "Remada baixa", 3, 15, 45),
                new Vaga("B", GrupoMuscular.PEITO, "Flexao de bracos", 3, 12, 45),
                new Vaga("C", GrupoMuscular.CARDIO, "Polichinelo", 3, 40, 30),
                new Vaga("C", GrupoMuscular.CORE, "Prancha abdominal", 3, 40, 30),
                new Vaga("C", GrupoMuscular.OMBROS, "Elevacao lateral", 3, 15, 45));
    }

    @Override
    protected int semanasDeValidade() {
        return 6;
    }
}

package school.cesar.gymflow.domain.treino.servico;

import java.util.List;

import school.cesar.gymflow.domain.treino.model.GrupoMuscular;

/** Condicionamento: treino equilibrado. */
public class GeradorCondicionamento extends GeradorDeFicha {

    @Override
    protected List<Vaga> vagas() {
        return List.of(
                new Vaga("A", GrupoMuscular.CARDIO, "Bicicleta ergometrica", 1, 20, 0),
                new Vaga("A", GrupoMuscular.PERNAS, "Leg press", 3, 12, 60),
                new Vaga("A", GrupoMuscular.CORE, "Abdominal supra", 3, 20, 30),
                new Vaga("B", GrupoMuscular.CARDIO, "Esteira em ritmo moderado", 1, 20, 0),
                new Vaga("B", GrupoMuscular.COSTAS, "Puxada frontal", 3, 12, 60),
                new Vaga("B", GrupoMuscular.PEITO, "Supino inclinado com halteres", 3, 12, 60),
                new Vaga("C", GrupoMuscular.CARDIO, "Polichinelo", 3, 30, 30),
                new Vaga("C", GrupoMuscular.PERNAS, "Afundo com halteres", 3, 12, 60),
                new Vaga("C", GrupoMuscular.CORE, "Prancha abdominal", 3, 30, 30));
    }

    @Override
    protected int semanasDeValidade() {
        return 4;
    }
}

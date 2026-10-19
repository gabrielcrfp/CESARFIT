package school.cesar.gymflow.domain.treino.servico;

import java.util.List;

import school.cesar.gymflow.domain.treino.model.GrupoMuscular;

/** Hipertrofia: cargas altas, poucas repeticoes, descanso longo. */
public class GeradorHipertrofia extends GeradorDeFicha {

    @Override
    protected List<Vaga> vagas() {
        return List.of(
                new Vaga("A", GrupoMuscular.PEITO, "Supino reto", 4, 8, 90),
                new Vaga("A", GrupoMuscular.PEITO, "Supino inclinado com halteres", 3, 10, 75),
                new Vaga("A", GrupoMuscular.BRACOS, "Triceps na polia", 3, 12, 60),
                new Vaga("B", GrupoMuscular.COSTAS, "Remada curvada", 4, 8, 90),
                new Vaga("B", GrupoMuscular.COSTAS, "Puxada frontal", 3, 10, 75),
                new Vaga("B", GrupoMuscular.BRACOS, "Rosca direta", 3, 12, 60),
                new Vaga("C", GrupoMuscular.PERNAS, "Agachamento livre", 4, 10, 120),
                new Vaga("C", GrupoMuscular.PERNAS, "Leg press", 3, 12, 90),
                new Vaga("C", GrupoMuscular.OMBROS, "Desenvolvimento militar", 3, 10, 75));
    }

    @Override
    protected int semanasDeValidade() {
        return 8;
    }
}

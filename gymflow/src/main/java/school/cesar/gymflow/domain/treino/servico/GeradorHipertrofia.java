package school.cesar.gymflow.domain.treino.servico;

import java.util.List;

import school.cesar.gymflow.domain.treino.model.GrupoMuscular;
import school.cesar.gymflow.domain.treino.model.ItemDeTreino;

/** Template Method - implementacao concreta para hipertrofia. */
public class GeradorHipertrofia extends GeradorDeFicha {

    @Override
    protected List<ItemDeTreino> montarItens() {
        return List.of(
                new ItemDeTreino("Supino reto", GrupoMuscular.PEITO, 4, 8, 90),
                new ItemDeTreino("Remada curvada", GrupoMuscular.COSTAS, 4, 8, 90),
                new ItemDeTreino("Agachamento livre", GrupoMuscular.PERNAS, 4, 10, 120),
                new ItemDeTreino("Desenvolvimento militar", GrupoMuscular.OMBROS, 3, 10, 75),
                new ItemDeTreino("Rosca direta", GrupoMuscular.BRACOS, 3, 12, 60));
    }

    @Override
    protected int semanasDeValidade() {
        return 8;
    }
}

package school.cesar.gymflow.domain.treino.servico;

import java.util.List;

import school.cesar.gymflow.domain.treino.model.GrupoMuscular;
import school.cesar.gymflow.domain.treino.model.ItemDeTreino;

/** Template Method - implementacao concreta para condicionamento geral. */
public class GeradorCondicionamento extends GeradorDeFicha {

    @Override
    protected List<ItemDeTreino> montarItens() {
        return List.of(
                new ItemDeTreino("Bicicleta ergometrica", GrupoMuscular.CARDIO, 1, 20, 0),
                new ItemDeTreino("Leg press", GrupoMuscular.PERNAS, 3, 12, 60),
                new ItemDeTreino("Puxada frontal", GrupoMuscular.COSTAS, 3, 12, 60),
                new ItemDeTreino("Abdominal supra", GrupoMuscular.CORE, 3, 20, 30));
    }

    @Override
    protected int semanasDeValidade() {
        return 4;
    }
}

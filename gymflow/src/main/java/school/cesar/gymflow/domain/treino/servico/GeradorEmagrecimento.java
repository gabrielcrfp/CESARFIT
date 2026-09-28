package school.cesar.gymflow.domain.treino.servico;

import java.util.List;

import school.cesar.gymflow.domain.treino.model.GrupoMuscular;
import school.cesar.gymflow.domain.treino.model.ItemDeTreino;

/** Template Method - implementacao concreta para emagrecimento. */
public class GeradorEmagrecimento extends GeradorDeFicha {

    @Override
    protected List<ItemDeTreino> montarItens() {
        return List.of(
                new ItemDeTreino("Esteira em ritmo moderado", GrupoMuscular.CARDIO, 1, 30, 0),
                new ItemDeTreino("Circuito de agachamento", GrupoMuscular.PERNAS, 3, 15, 45),
                new ItemDeTreino("Remada baixa", GrupoMuscular.COSTAS, 3, 15, 45),
                new ItemDeTreino("Prancha abdominal", GrupoMuscular.CORE, 3, 40, 30));
    }

    @Override
    protected int semanasDeValidade() {
        return 6;
    }
}

package school.cesar.gymflow.domain.treino.model;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Entidade interna do agregado FichaDeTreino. */
public class ItemDeTreino {

    private final String exercicio;
    private final GrupoMuscular grupoMuscular;
    private final int series;
    private final int repeticoes;
    private final int descansoEmSegundos;
    private final String divisao;
    private boolean concluido;

    public ItemDeTreino(String exercicio, GrupoMuscular grupoMuscular,
                        int series, int repeticoes, int descansoEmSegundos) {
        this(exercicio, grupoMuscular, series, repeticoes, descansoEmSegundos, "A");
    }

    /** divisao = dia do treino (A, B ou C). */
    public ItemDeTreino(String exercicio, GrupoMuscular grupoMuscular,
                        int series, int repeticoes, int descansoEmSegundos, String divisao) {
        if (series <= 0 || repeticoes <= 0) {
            throw new RegraDeNegocioException("Series e repeticoes devem ser positivas");
        }
        this.exercicio = exercicio;
        this.grupoMuscular = grupoMuscular;
        this.series = series;
        this.repeticoes = repeticoes;
        this.descansoEmSegundos = descansoEmSegundos;
        this.divisao = FichaDeTreino.divisaoValida(divisao);
        this.concluido = false;
    }

    public void concluir() {
        if (concluido) {
            throw new RegraDeNegocioException("Item ja concluido: " + exercicio);
        }
        this.concluido = true;
    }

    public void reabrir() {
        this.concluido = false;
    }

    public String exercicio() {
        return exercicio;
    }

    public GrupoMuscular grupoMuscular() {
        return grupoMuscular;
    }

    public int series() {
        return series;
    }

    public int repeticoes() {
        return repeticoes;
    }

    public int descansoEmSegundos() {
        return descansoEmSegundos;
    }

    public String divisao() {
        return divisao;
    }

    public boolean concluido() {
        return concluido;
    }
}

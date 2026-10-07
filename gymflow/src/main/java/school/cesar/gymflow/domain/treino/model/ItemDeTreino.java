package school.cesar.gymflow.domain.treino.model;

import java.time.LocalDate;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Entidade interna do agregado FichaDeTreino. */
public class ItemDeTreino {

    private final String exercicio;
    private final GrupoMuscular grupoMuscular;
    private final int series;
    private final int repeticoes;
    private final int descansoEmSegundos;
    private boolean concluido;
    private LocalDate concluidoEm;

    public ItemDeTreino(String exercicio, GrupoMuscular grupoMuscular,
                        int series, int repeticoes, int descansoEmSegundos) {
        if (series <= 0 || repeticoes <= 0) {
            throw new RegraDeNegocioException("Series e repeticoes devem ser positivas");
        }
        this.exercicio = exercicio;
        this.grupoMuscular = grupoMuscular;
        this.series = series;
        this.repeticoes = repeticoes;
        this.descansoEmSegundos = descansoEmSegundos;
        this.concluido = false;
        this.concluidoEm = null;
    }

    public ItemDeTreino(String exercicio, GrupoMuscular grupoMuscular, int series,
                        int repeticoes, int descansoEmSegundos, boolean concluido,
                        LocalDate concluidoEm) {
        this(exercicio, grupoMuscular, series, repeticoes, descansoEmSegundos);
        this.concluido = concluido;
        this.concluidoEm = concluido ? (concluidoEm == null ? LocalDate.now() : concluidoEm) : null;
    }

    public void concluir() {
        if (concluidoHoje()) {
            throw new RegraDeNegocioException("Item ja concluido: " + exercicio);
        }
        this.concluido = true;
        this.concluidoEm = LocalDate.now();
    }

    public void reabrir() {
        this.concluido = false;
        this.concluidoEm = null;
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

    public boolean concluido() {
        return concluidoHoje();
    }

    public boolean concluidoHoje() {
        return concluido && LocalDate.now().equals(concluidoEm);
    }

    public LocalDate concluidoEm() { return concluidoEm; }
}

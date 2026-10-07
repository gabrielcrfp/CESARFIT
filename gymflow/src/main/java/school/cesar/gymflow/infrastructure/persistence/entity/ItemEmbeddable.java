package school.cesar.gymflow.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.LocalDate;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import school.cesar.gymflow.domain.treino.model.GrupoMuscular;

/** Item da ficha mapeado como colecao de elementos do agregado FichaDeTreino. */
@Embeddable
public class ItemEmbeddable {

    @Column(nullable = false)
    private String exercicio;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_muscular", nullable = false)
    private GrupoMuscular grupoMuscular;

    @Column(nullable = false)
    private int series;

    @Column(nullable = false)
    private int repeticoes;

    @Column(name = "descanso_segundos", nullable = false)
    private int descansoEmSegundos;

    @Column(nullable = false)
    private boolean concluido;

    @Column(name = "concluido_em")
    private LocalDate concluidoEm;

    protected ItemEmbeddable() {
    }

    public ItemEmbeddable(String exercicio, GrupoMuscular grupoMuscular, int series,
                          int repeticoes, int descansoEmSegundos, boolean concluido) {
        this(exercicio, grupoMuscular, series, repeticoes, descansoEmSegundos, concluido, null);
    }

    public ItemEmbeddable(String exercicio, GrupoMuscular grupoMuscular, int series,
                          int repeticoes, int descansoEmSegundos, boolean concluido,
                          LocalDate concluidoEm) {
        this.exercicio = exercicio;
        this.grupoMuscular = grupoMuscular;
        this.series = series;
        this.repeticoes = repeticoes;
        this.descansoEmSegundos = descansoEmSegundos;
        this.concluido = concluido;
        this.concluidoEm = concluidoEm;
    }

    public String getExercicio() {
        return exercicio;
    }

    public GrupoMuscular getGrupoMuscular() {
        return grupoMuscular;
    }

    public int getSeries() {
        return series;
    }

    public int getRepeticoes() {
        return repeticoes;
    }

    public int getDescansoEmSegundos() {
        return descansoEmSegundos;
    }

    public boolean isConcluido() {
        return concluido;
    }

    public LocalDate getConcluidoEm() { return concluidoEm; }
}

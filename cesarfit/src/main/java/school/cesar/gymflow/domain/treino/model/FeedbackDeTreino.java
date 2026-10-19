package school.cesar.gymflow.domain.treino.model;

import java.time.LocalDate;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.shared.Texto;

/** Percepcao do aluno ao fim do treino: esforco de 1 (muito leve) a 10 (maximo) e observacoes. */
public record FeedbackDeTreino(Long id, Long alunoId, Long fichaId, LocalDate data, int esforco,
                               String observacao, int exerciciosConcluidos, int totalDeExercicios) {

    public FeedbackDeTreino {
        if (alunoId == null || fichaId == null) {
            throw new RegraDeNegocioException("O feedback deve estar vinculado ao aluno e a ficha");
        }
        if (esforco < 1 || esforco > 10) {
            throw new RegraDeNegocioException("A percepcao de esforco deve ser de 1 a 10");
        }
        if (exerciciosConcluidos < 1) {
            throw new RegraDeNegocioException("Conclua ao menos um exercicio antes de registrar o feedback");
        }
        observacao = Texto.opcional(observacao, 500, "Observacao");
    }
}

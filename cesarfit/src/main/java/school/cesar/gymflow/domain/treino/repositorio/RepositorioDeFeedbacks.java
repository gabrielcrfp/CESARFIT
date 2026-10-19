package school.cesar.gymflow.domain.treino.repositorio;

import java.time.LocalDate;
import java.util.List;

import school.cesar.gymflow.domain.treino.model.FeedbackDeTreino;

/** Porta de saida (arquitetura limpa). */
public interface RepositorioDeFeedbacks {

    FeedbackDeTreino salvar(FeedbackDeTreino feedback);

    boolean existe(Long alunoId, LocalDate data);

    List<FeedbackDeTreino> porAluno(Long alunoId);
}

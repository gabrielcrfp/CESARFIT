package school.cesar.gymflow.infrastructure.persistence.adapter;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import school.cesar.gymflow.domain.treino.model.FeedbackDeTreino;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFeedbacks;
import school.cesar.gymflow.infrastructure.persistence.entity.FeedbackEntity;
import school.cesar.gymflow.infrastructure.persistence.springdata.FeedbackJpaRepository;

/** Adaptador de saida dos feedbacks pos-treino. */
@Repository
public class FeedbackRepositorioJpa implements RepositorioDeFeedbacks {

    private final FeedbackJpaRepository jpa;

    public FeedbackRepositorioJpa(FeedbackJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public FeedbackDeTreino salvar(FeedbackDeTreino f) {
        FeedbackEntity e = new FeedbackEntity();
        e.id = f.id();
        e.alunoId = f.alunoId();
        e.fichaId = f.fichaId();
        e.data = f.data();
        e.esforco = f.esforco();
        e.observacao = f.observacao();
        e.exerciciosConcluidos = f.exerciciosConcluidos();
        e.totalDeExercicios = f.totalDeExercicios();
        return paraDominio(jpa.save(e));
    }

    @Override
    public boolean existe(Long alunoId, LocalDate data) {
        return jpa.existsByAlunoIdAndData(alunoId, data);
    }

    @Override
    public List<FeedbackDeTreino> porAluno(Long alunoId) {
        return jpa.findByAlunoIdOrderByDataDesc(alunoId).stream().map(FeedbackRepositorioJpa::paraDominio).toList();
    }

    private static FeedbackDeTreino paraDominio(FeedbackEntity e) {
        return new FeedbackDeTreino(e.id, e.alunoId, e.fichaId, e.data, e.esforco, e.observacao,
                e.exerciciosConcluidos, e.totalDeExercicios);
    }
}

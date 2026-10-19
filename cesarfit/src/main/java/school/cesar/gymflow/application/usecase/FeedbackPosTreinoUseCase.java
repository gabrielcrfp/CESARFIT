package school.cesar.gymflow.application.usecase;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.treino.model.FeedbackDeTreino;
import school.cesar.gymflow.domain.treino.model.FichaDeTreino;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFeedbacks;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFichas;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDePresencas;

/**
 * Funcionalidade: registro de feedback pos-treino.
 * Exige check-in no dia e ao menos um exercicio concluido; um feedback por dia.
 */
@Service
public class FeedbackPosTreinoUseCase {

    private final RepositorioDeFeedbacks feedbacks;
    private final RepositorioDeFichas fichas;
    private final RepositorioDePresencas presencas;
    private final PublicadorDeEventos publicador;

    public FeedbackPosTreinoUseCase(RepositorioDeFeedbacks feedbacks, RepositorioDeFichas fichas,
                                    RepositorioDePresencas presencas, PublicadorDeEventos publicador) {
        this.feedbacks = feedbacks;
        this.fichas = fichas;
        this.presencas = presencas;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.FeedbackResumo registrar(Long alunoId, Comandos.RegistrarFeedback comando) {
        LocalDate hoje = LocalDate.now();
        if (!presencas.existe(alunoId, hoje)) {
            throw new RegraDeNegocioException("Faca o check-in de hoje antes de registrar o feedback");
        }
        if (feedbacks.existe(alunoId, hoje)) {
            throw new RegraDeNegocioException("O feedback do treino de hoje ja foi registrado");
        }
        FichaDeTreino ficha = fichas.porAluno(alunoId).stream().findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Voce ainda nao tem ficha de treino"));
        int total = ficha.itens().size();
        int concluidos = total - ficha.totalPendentes();
        FeedbackDeTreino salvo = feedbacks.salvar(new FeedbackDeTreino(null, alunoId, ficha.id(), hoje,
                comando.esforco(), comando.observacao(), concluidos, total));
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.FEEDBACK_TREINO, String.valueOf(salvo.id()),
                "Aluno " + alunoId + " avaliou o treino com esforco " + salvo.esforco() + "/10"));
        return resumo(salvo);
    }

    @Transactional(readOnly = true)
    public List<Respostas.FeedbackResumo> doAluno(Long alunoId) {
        return feedbacks.porAluno(alunoId).stream().map(FeedbackPosTreinoUseCase::resumo).toList();
    }

    @Transactional(readOnly = true)
    public boolean registradoHoje(Long alunoId) {
        return feedbacks.existe(alunoId, LocalDate.now());
    }

    static Respostas.FeedbackResumo resumo(FeedbackDeTreino f) {
        return new Respostas.FeedbackResumo(f.id(), f.alunoId(), f.data(), f.esforco(), f.observacao(),
                f.exerciciosConcluidos(), f.totalDeExercicios());
    }
}

package school.cesar.gymflow.domain.matricula.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import school.cesar.gymflow.domain.matricula.model.NotificacaoDeVencimento;

/** Porta de saida (arquitetura limpa). */
public interface RepositorioDeNotificacoes {

    NotificacaoDeVencimento salvar(NotificacaoDeVencimento notificacao);

    Optional<NotificacaoDeVencimento> porId(Long id);

    boolean existe(Long matriculaId, LocalDate vencimento);

    List<NotificacaoDeVencimento> porAluno(Long alunoId);

    List<NotificacaoDeVencimento> todas();
}

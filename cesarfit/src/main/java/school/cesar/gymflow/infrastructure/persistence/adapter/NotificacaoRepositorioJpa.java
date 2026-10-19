package school.cesar.gymflow.infrastructure.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import school.cesar.gymflow.domain.matricula.model.NotificacaoDeVencimento;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeNotificacoes;
import school.cesar.gymflow.infrastructure.persistence.entity.NotificacaoEntity;
import school.cesar.gymflow.infrastructure.persistence.springdata.NotificacaoJpaRepository;

/** Adaptador de saida das notificacoes de vencimento. */
@Repository
public class NotificacaoRepositorioJpa implements RepositorioDeNotificacoes {

    private final NotificacaoJpaRepository jpa;

    public NotificacaoRepositorioJpa(NotificacaoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public NotificacaoDeVencimento salvar(NotificacaoDeVencimento n) {
        NotificacaoEntity e = new NotificacaoEntity();
        e.id = n.id();
        e.matriculaId = n.matriculaId();
        e.alunoId = n.alunoId();
        e.vencimento = n.vencimento();
        e.enviadaEm = n.enviadaEm();
        e.mensagem = n.mensagem();
        e.lida = n.lida();
        return paraDominio(jpa.save(e));
    }

    @Override
    public Optional<NotificacaoDeVencimento> porId(Long id) {
        return jpa.findById(id).map(NotificacaoRepositorioJpa::paraDominio);
    }

    @Override
    public boolean existe(Long matriculaId, LocalDate vencimento) {
        return jpa.existsByMatriculaIdAndVencimento(matriculaId, vencimento);
    }

    @Override
    public List<NotificacaoDeVencimento> porAluno(Long alunoId) {
        return jpa.findByAlunoIdOrderByEnviadaEmDesc(alunoId).stream().map(NotificacaoRepositorioJpa::paraDominio).toList();
    }

    @Override
    public List<NotificacaoDeVencimento> todas() {
        return jpa.findAllByOrderByEnviadaEmDesc().stream().map(NotificacaoRepositorioJpa::paraDominio).toList();
    }

    private static NotificacaoDeVencimento paraDominio(NotificacaoEntity e) {
        return new NotificacaoDeVencimento(e.id, e.matriculaId, e.alunoId, e.vencimento, e.enviadaEm, e.mensagem, e.lida);
    }
}

package school.cesar.gymflow.application.usecase;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.model.NotificacaoDeVencimento;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeNotificacoes;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Funcionalidade: notificacao automatica de vencimento de matricula.
 * Roda todo dia (agendador) ou sob demanda; avisa matriculas ativas que vencem em ate 3 dias
 * e registra cada aviso para nao repetir o envio para o mesmo vencimento.
 */
@Service
public class NotificacaoDeVencimentoUseCase {

    private final RepositorioDeMatriculas matriculas;
    private final RepositorioDeNotificacoes notificacoes;
    private final NomesDeAlunos nomes;
    private final PublicadorDeEventos publicador;

    public NotificacaoDeVencimentoUseCase(RepositorioDeMatriculas matriculas, RepositorioDeNotificacoes notificacoes,
                                          NomesDeAlunos nomes, PublicadorDeEventos publicador) {
        this.matriculas = matriculas;
        this.notificacoes = notificacoes;
        this.nomes = nomes;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.VerificacaoDeVencimentos verificar(LocalDate hoje) {
        Map<Long, String> mapa = nomes.mapa();
        List<Respostas.NotificacaoResumo> enviados = new ArrayList<>();
        for (Matricula matricula : matriculas.todas()) {
            if (!NotificacaoDeVencimento.deveAvisar(matricula, hoje)
                    || notificacoes.existe(matricula.id(), matricula.fim())) {
                continue;
            }
            String nome = mapa.getOrDefault(matricula.alunoId(), "aluno");
            NotificacaoDeVencimento salva = notificacoes.salvar(NotificacaoDeVencimento.para(matricula, nome, hoje));
            publicador.publicar(EventoDeDominio.de(EventoDeDominio.MATRICULA_A_VENCER,
                    String.valueOf(matricula.id()), salva.mensagem()));
            enviados.add(resumo(salva, nome));
        }
        return new Respostas.VerificacaoDeVencimentos(enviados.size(), enviados);
    }

    @Transactional
    public Respostas.NotificacaoResumo marcarComoLida(Long alunoId, Long notificacaoId) {
        NotificacaoDeVencimento n = notificacoes.porId(notificacaoId)
                .filter(x -> x.alunoId().equals(alunoId))
                .orElseThrow(() -> new RegraDeNegocioException("Aviso nao encontrado"));
        n.marcarComoLida();
        return resumo(notificacoes.salvar(n), nomes.de(alunoId));
    }

    @Transactional(readOnly = true)
    public List<Respostas.NotificacaoResumo> doAluno(Long alunoId) {
        String nome = nomes.de(alunoId);
        return notificacoes.porAluno(alunoId).stream().map(n -> resumo(n, nome)).toList();
    }

    @Transactional(readOnly = true)
    public List<Respostas.NotificacaoResumo> todas() {
        Map<Long, String> mapa = nomes.mapa();
        return notificacoes.todas().stream().map(n -> resumo(n, mapa.getOrDefault(n.alunoId(), "-"))).toList();
    }

    static Respostas.NotificacaoResumo resumo(NotificacaoDeVencimento n, String nome) {
        return new Respostas.NotificacaoResumo(n.id(), n.matriculaId(), n.alunoId(), nome, n.vencimento(),
                n.enviadaEm(), n.mensagem(), n.lida());
    }
}

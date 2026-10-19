package school.cesar.gymflow.application.usecase;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.atendimento.model.SolicitacaoDeAvaliacao;
import school.cesar.gymflow.domain.atendimento.repositorio.RepositorioDeSolicitacoes;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Funcionalidade: solicitacao de avaliacao fisica (aluno pede, equipe confirma ou propoe outro horario). */
@Service
public class AvaliacaoFisicaUseCase {

    private final RepositorioDeSolicitacoes solicitacoes;
    private final RepositorioDeMatriculas matriculas;
    private final NomesDeAlunos nomes;
    private final PublicadorDeEventos publicador;

    public AvaliacaoFisicaUseCase(RepositorioDeSolicitacoes solicitacoes, RepositorioDeMatriculas matriculas,
                                  NomesDeAlunos nomes, PublicadorDeEventos publicador) {
        this.solicitacoes = solicitacoes;
        this.matriculas = matriculas;
        this.nomes = nomes;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.AvaliacaoResumo solicitar(Long alunoId, Comandos.SolicitarAvaliacao comando) {
        if (matriculas.vigentePorAluno(alunoId).isEmpty()) {
            throw new RegraDeNegocioException("Avaliacao fisica exige matricula ativa");
        }
        if (solicitacoes.avaliacoesDoAluno(alunoId).stream().anyMatch(SolicitacaoDeAvaliacao::emAberto)) {
            throw new RegraDeNegocioException("Voce ja tem uma avaliacao fisica em aberto");
        }
        return salvar(SolicitacaoDeAvaliacao.solicitar(alunoId, comando.dataHora()), "solicitada");
    }

    @Transactional
    public Respostas.AvaliacaoResumo confirmar(Long id, String observacao) {
        SolicitacaoDeAvaliacao s = buscar(id);
        s.confirmar(observacao);
        return salvar(s, "confirmada para " + s.dataAgendada());
    }

    @Transactional
    public Respostas.AvaliacaoResumo proporHorario(Long id, Comandos.ResponderAvaliacao comando) {
        SolicitacaoDeAvaliacao s = buscar(id);
        s.proporNovoHorario(comando.novaDataHora(), comando.observacao());
        return salvar(s, "novo horario proposto: " + s.dataProposta());
    }

    @Transactional
    public Respostas.AvaliacaoResumo aceitarProposta(Long alunoId, Long id) {
        SolicitacaoDeAvaliacao s = doAluno(alunoId, id);
        s.aceitarProposta();
        return salvar(s, "proposta aceita pelo aluno");
    }

    @Transactional
    public Respostas.AvaliacaoResumo cancelar(Long alunoId, Long id) {
        SolicitacaoDeAvaliacao s = doAluno(alunoId, id);
        s.cancelar();
        return salvar(s, "cancelada pelo aluno");
    }

    @Transactional(readOnly = true)
    public List<Respostas.AvaliacaoResumo> doAluno(Long alunoId) {
        String nome = nomes.de(alunoId);
        return solicitacoes.avaliacoesDoAluno(alunoId).stream().map(a -> resumo(a, nome)).toList();
    }

    @Transactional(readOnly = true)
    public List<Respostas.AvaliacaoResumo> todas() {
        Map<Long, String> mapa = nomes.mapa();
        return solicitacoes.avaliacoes().stream().map(a -> resumo(a, mapa.getOrDefault(a.alunoId(), "-"))).toList();
    }

    private SolicitacaoDeAvaliacao buscar(Long id) {
        return solicitacoes.avaliacaoPorId(id)
                .orElseThrow(() -> new RegraDeNegocioException("Solicitacao de avaliacao nao encontrada"));
    }

    private SolicitacaoDeAvaliacao doAluno(Long alunoId, Long id) {
        SolicitacaoDeAvaliacao s = buscar(id);
        if (!s.alunoId().equals(alunoId)) {
            throw new RegraDeNegocioException("Solicitacao de avaliacao nao encontrada");
        }
        return s;
    }

    private Respostas.AvaliacaoResumo salvar(SolicitacaoDeAvaliacao s, String detalhe) {
        SolicitacaoDeAvaliacao salva = solicitacoes.salvar(s);
        String nome = nomes.de(salva.alunoId());
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.AVALIACAO_FISICA, String.valueOf(salva.id()),
                "Avaliacao fisica de " + nome + " " + detalhe));
        return resumo(salva, nome);
    }

    static Respostas.AvaliacaoResumo resumo(SolicitacaoDeAvaliacao a, String nome) {
        return new Respostas.AvaliacaoResumo(a.id(), a.alunoId(), nome, a.dataDesejada(), a.dataProposta(),
                a.dataAgendada(), a.status().name(), a.observacao(), a.emAberto(), a.criadaEm());
    }
}

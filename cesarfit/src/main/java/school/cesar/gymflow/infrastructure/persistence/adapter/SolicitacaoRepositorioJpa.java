package school.cesar.gymflow.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import school.cesar.gymflow.domain.atendimento.model.SolicitacaoDeAtendimento;
import school.cesar.gymflow.domain.atendimento.model.SolicitacaoDeAvaliacao;
import school.cesar.gymflow.domain.atendimento.model.SolicitacaoDeSubstituicao;
import school.cesar.gymflow.domain.atendimento.repositorio.RepositorioDeSolicitacoes;
import school.cesar.gymflow.infrastructure.persistence.entity.AtendimentoEntity;
import school.cesar.gymflow.infrastructure.persistence.entity.AvaliacaoEntity;
import school.cesar.gymflow.infrastructure.persistence.entity.SubstituicaoEntity;
import school.cesar.gymflow.infrastructure.persistence.springdata.AtendimentoJpaRepository;
import school.cesar.gymflow.infrastructure.persistence.springdata.AvaliacaoJpaRepository;
import school.cesar.gymflow.infrastructure.persistence.springdata.SubstituicaoJpaRepository;

/** Adaptador de saida das solicitacoes dos alunos (avaliacao, substituicao e atendimento). */
@Repository
public class SolicitacaoRepositorioJpa implements RepositorioDeSolicitacoes {

    private final AvaliacaoJpaRepository avaliacoes;
    private final SubstituicaoJpaRepository substituicoes;
    private final AtendimentoJpaRepository atendimentos;

    public SolicitacaoRepositorioJpa(AvaliacaoJpaRepository avaliacoes, SubstituicaoJpaRepository substituicoes,
                                     AtendimentoJpaRepository atendimentos) {
        this.avaliacoes = avaliacoes;
        this.substituicoes = substituicoes;
        this.atendimentos = atendimentos;
    }

    /* ---------- avaliacao ---------- */

    @Override
    public SolicitacaoDeAvaliacao salvar(SolicitacaoDeAvaliacao s) {
        AvaliacaoEntity e = new AvaliacaoEntity();
        e.id = s.id();
        e.alunoId = s.alunoId();
        e.dataDesejada = s.dataDesejada();
        e.dataProposta = s.dataProposta();
        e.dataAgendada = s.dataAgendada();
        e.status = s.status();
        e.observacao = s.observacao();
        e.criadaEm = s.criadaEm();
        return avaliacao(avaliacoes.save(e));
    }

    @Override
    public Optional<SolicitacaoDeAvaliacao> avaliacaoPorId(Long id) {
        return avaliacoes.findById(id).map(SolicitacaoRepositorioJpa::avaliacao);
    }

    @Override
    public List<SolicitacaoDeAvaliacao> avaliacoesDoAluno(Long alunoId) {
        return avaliacoes.findByAlunoIdOrderByCriadaEmDesc(alunoId).stream().map(SolicitacaoRepositorioJpa::avaliacao).toList();
    }

    @Override
    public List<SolicitacaoDeAvaliacao> avaliacoes() {
        return avaliacoes.findAllByOrderByCriadaEmDesc().stream().map(SolicitacaoRepositorioJpa::avaliacao).toList();
    }

    private static SolicitacaoDeAvaliacao avaliacao(AvaliacaoEntity e) {
        return new SolicitacaoDeAvaliacao(e.id, e.alunoId, e.dataDesejada, e.dataProposta, e.dataAgendada,
                e.status, e.observacao, e.criadaEm);
    }

    /* ---------- substituicao ---------- */

    @Override
    public SolicitacaoDeSubstituicao salvar(SolicitacaoDeSubstituicao s) {
        SubstituicaoEntity e = new SubstituicaoEntity();
        e.id = s.id();
        e.alunoId = s.alunoId();
        e.fichaId = s.fichaId();
        e.posicao = s.posicao();
        e.exercicioAtual = s.exercicioAtual();
        e.motivo = s.motivo();
        e.status = s.status();
        e.exercicioNovo = s.exercicioNovo();
        e.resposta = s.resposta();
        e.criadaEm = s.criadaEm();
        e.respondidaEm = s.respondidaEm();
        return substituicao(substituicoes.save(e));
    }

    @Override
    public Optional<SolicitacaoDeSubstituicao> substituicaoPorId(Long id) {
        return substituicoes.findById(id).map(SolicitacaoRepositorioJpa::substituicao);
    }

    @Override
    public List<SolicitacaoDeSubstituicao> substituicoesDoAluno(Long alunoId) {
        return substituicoes.findByAlunoIdOrderByCriadaEmDesc(alunoId).stream().map(SolicitacaoRepositorioJpa::substituicao).toList();
    }

    @Override
    public List<SolicitacaoDeSubstituicao> substituicoes() {
        return substituicoes.findAllByOrderByCriadaEmDesc().stream().map(SolicitacaoRepositorioJpa::substituicao).toList();
    }

    private static SolicitacaoDeSubstituicao substituicao(SubstituicaoEntity e) {
        return new SolicitacaoDeSubstituicao(e.id, e.alunoId, e.fichaId, e.posicao, e.exercicioAtual, e.motivo,
                e.status, e.exercicioNovo, e.resposta, e.criadaEm, e.respondidaEm);
    }

    /* ---------- atendimento ---------- */

    @Override
    public SolicitacaoDeAtendimento salvar(SolicitacaoDeAtendimento s) {
        AtendimentoEntity e = new AtendimentoEntity();
        e.id = s.id();
        e.alunoId = s.alunoId();
        e.fichaId = s.fichaId();
        e.exercicio = s.exercicio();
        e.motivo = s.motivo();
        e.status = s.status();
        e.instrutor = s.instrutor();
        e.orientacao = s.orientacao();
        e.abertaEm = s.abertaEm();
        e.concluidaEm = s.concluidaEm();
        return atendimento(atendimentos.save(e));
    }

    @Override
    public Optional<SolicitacaoDeAtendimento> atendimentoPorId(Long id) {
        return atendimentos.findById(id).map(SolicitacaoRepositorioJpa::atendimento);
    }

    @Override
    public List<SolicitacaoDeAtendimento> atendimentosDoAluno(Long alunoId) {
        return atendimentos.findByAlunoIdOrderByAbertaEmDesc(alunoId).stream().map(SolicitacaoRepositorioJpa::atendimento).toList();
    }

    @Override
    public List<SolicitacaoDeAtendimento> atendimentos() {
        return atendimentos.findAllByOrderByAbertaEmDesc().stream().map(SolicitacaoRepositorioJpa::atendimento).toList();
    }

    private static SolicitacaoDeAtendimento atendimento(AtendimentoEntity e) {
        return new SolicitacaoDeAtendimento(e.id, e.alunoId, e.fichaId, e.exercicio, e.motivo, e.status,
                e.instrutor, e.orientacao, e.abertaEm, e.concluidaEm);
    }
}

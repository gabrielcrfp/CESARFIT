package school.cesar.gymflow.domain.atendimento.repositorio;

import java.util.List;
import java.util.Optional;

import school.cesar.gymflow.domain.atendimento.model.SolicitacaoDeAtendimento;
import school.cesar.gymflow.domain.atendimento.model.SolicitacaoDeAvaliacao;
import school.cesar.gymflow.domain.atendimento.model.SolicitacaoDeSubstituicao;

/** Porta de saida das solicitacoes dos alunos (arquitetura limpa). */
public interface RepositorioDeSolicitacoes {

    SolicitacaoDeAvaliacao salvar(SolicitacaoDeAvaliacao avaliacao);

    Optional<SolicitacaoDeAvaliacao> avaliacaoPorId(Long id);

    List<SolicitacaoDeAvaliacao> avaliacoesDoAluno(Long alunoId);

    List<SolicitacaoDeAvaliacao> avaliacoes();

    SolicitacaoDeSubstituicao salvar(SolicitacaoDeSubstituicao substituicao);

    Optional<SolicitacaoDeSubstituicao> substituicaoPorId(Long id);

    List<SolicitacaoDeSubstituicao> substituicoesDoAluno(Long alunoId);

    List<SolicitacaoDeSubstituicao> substituicoes();

    SolicitacaoDeAtendimento salvar(SolicitacaoDeAtendimento atendimento);

    Optional<SolicitacaoDeAtendimento> atendimentoPorId(Long id);

    List<SolicitacaoDeAtendimento> atendimentosDoAluno(Long alunoId);

    List<SolicitacaoDeAtendimento> atendimentos();
}

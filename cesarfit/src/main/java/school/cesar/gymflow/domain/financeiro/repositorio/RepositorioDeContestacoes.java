package school.cesar.gymflow.domain.financeiro.repositorio;

import java.util.List;
import java.util.Optional;

import school.cesar.gymflow.domain.financeiro.model.ContestacaoDeCobranca;

/** Porta de saida (arquitetura limpa). */
public interface RepositorioDeContestacoes {

    ContestacaoDeCobranca salvar(ContestacaoDeCobranca contestacao);

    Optional<ContestacaoDeCobranca> porId(Long id);

    List<ContestacaoDeCobranca> porAluno(Long alunoId);

    List<ContestacaoDeCobranca> todas();
}

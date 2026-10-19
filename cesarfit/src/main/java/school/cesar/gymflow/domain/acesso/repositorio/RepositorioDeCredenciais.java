package school.cesar.gymflow.domain.acesso.repositorio;

import java.util.Optional;

import school.cesar.gymflow.domain.acesso.model.Credencial;

/** Porta de saida (arquitetura limpa). */
public interface RepositorioDeCredenciais {

    Credencial salvar(Credencial credencial);

    Optional<Credencial> daEquipe(String login);

    Optional<Credencial> doAluno(Long alunoId);
}

package school.cesar.gymflow.domain.matricula.repositorio;

import java.util.List;
import java.util.Optional;

import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.shared.Cpf;

/** Porta de saida (arquitetura limpa). */
public interface RepositorioDeAlunos {

    Aluno salvar(Aluno aluno);

    Optional<Aluno> porId(Long id);

    Optional<Aluno> porCpf(Cpf cpf);

    Optional<Aluno> porEmail(String email);

    List<Aluno> todos();

    long quantidade();

    void excluir(Long id);
}

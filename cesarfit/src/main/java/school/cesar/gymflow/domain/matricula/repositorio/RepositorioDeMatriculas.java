package school.cesar.gymflow.domain.matricula.repositorio;

import java.util.List;
import java.util.Optional;

import school.cesar.gymflow.domain.matricula.model.Matricula;

/** Porta de saida (arquitetura limpa). */
public interface RepositorioDeMatriculas {

    Matricula salvar(Matricula matricula);

    Optional<Matricula> porId(Long id);

    Optional<Matricula> vigentePorAluno(Long alunoId);

    List<Matricula> porAluno(Long alunoId);

    List<Matricula> todas();

    void excluir(Long id);
}

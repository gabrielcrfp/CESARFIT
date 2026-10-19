package school.cesar.gymflow.domain.treino.repositorio;

import java.util.List;
import java.util.Optional;

import school.cesar.gymflow.domain.treino.model.FichaDeTreino;

/** Porta de saida (arquitetura limpa). */
public interface RepositorioDeFichas {

    FichaDeTreino salvar(FichaDeTreino ficha);

    Optional<FichaDeTreino> porId(Long id);

    List<FichaDeTreino> porAluno(Long alunoId);

    void excluirPorAluno(Long alunoId);

    boolean usaExercicio(String nomeDoExercicio);
}

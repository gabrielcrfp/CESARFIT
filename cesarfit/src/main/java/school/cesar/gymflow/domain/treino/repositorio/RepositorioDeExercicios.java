package school.cesar.gymflow.domain.treino.repositorio;

import java.util.List;
import java.util.Optional;

import school.cesar.gymflow.domain.treino.model.Exercicio;

/** Porta de saida (arquitetura limpa). */
public interface RepositorioDeExercicios {

    Exercicio salvar(Exercicio exercicio);

    Optional<Exercicio> porId(Long id);

    Optional<Exercicio> porNome(String nome);

    List<Exercicio> todos();

    List<Exercicio> ativos();

    void excluir(Long id);

    long quantidade();
}

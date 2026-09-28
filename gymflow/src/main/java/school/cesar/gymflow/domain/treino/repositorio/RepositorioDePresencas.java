package school.cesar.gymflow.domain.treino.repositorio;

import java.time.LocalDate;
import java.util.List;

import school.cesar.gymflow.domain.treino.model.Presenca;

/** Porta de saida (arquitetura limpa). */
public interface RepositorioDePresencas {

    Presenca salvar(Presenca presenca);

    boolean existe(Long alunoId, LocalDate data);

    List<Presenca> porAluno(Long alunoId);
}

package school.cesar.gymflow.application.usecase;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;

/** Resolve o nome do aluno para as listagens (com cache por chamada). */
@Component
public class NomesDeAlunos {

    private final RepositorioDeAlunos alunos;

    public NomesDeAlunos(RepositorioDeAlunos alunos) {
        this.alunos = alunos;
    }

    public Map<Long, String> mapa() {
        Map<Long, String> nomes = new HashMap<>();
        alunos.todos().forEach(a -> nomes.put(a.id(), a.nome()));
        return nomes;
    }

    public String de(Long alunoId) {
        return alunoId == null ? "-" : alunos.porId(alunoId).map(Aluno::nome).orElse("-");
    }
}

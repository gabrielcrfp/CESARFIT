package school.cesar.gymflow.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Caso de uso: renovar matricula (complexidade baixa). */
@Service
public class RenovarMatriculaUseCase {

    private final RepositorioDeMatriculas repositorioDeMatriculas;
    private final RepositorioDeAlunos repositorioDeAlunos;
    private final PublicadorDeEventos publicador;

    public RenovarMatriculaUseCase(RepositorioDeMatriculas repositorioDeMatriculas,
            RepositorioDeAlunos repositorioDeAlunos,
                                   PublicadorDeEventos publicador) {
        this.repositorioDeMatriculas = repositorioDeMatriculas;
        this.repositorioDeAlunos = repositorioDeAlunos;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.MatriculaResumo executar(Long matriculaId) {
        Matricula matricula = repositorioDeMatriculas.porId(matriculaId)
                .orElseThrow(() -> new RegraDeNegocioException("Matricula nao encontrada"));
        matricula.renovar();
        Matricula salva = repositorioDeMatriculas.salvar(matricula);
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.MATRICULA_RENOVADA,
                String.valueOf(salva.id()), "Nova vigencia ate " + salva.fim()));
        String nome = repositorioDeAlunos.porId(salva.alunoId()).map(Aluno::nome).orElse("-");
        return Resumos.matricula(salva, nome);
    }
}

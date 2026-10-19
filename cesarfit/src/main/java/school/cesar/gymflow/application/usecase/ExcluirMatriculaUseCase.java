package school.cesar.gymflow.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Caso de uso: excluir matricula definitivamente (baixa - Integrante 4). */
@Service
public class ExcluirMatriculaUseCase {

    private final RepositorioDeMatriculas repositorioDeMatriculas;
    private final PublicadorDeEventos publicador;

    public ExcluirMatriculaUseCase(RepositorioDeMatriculas repositorioDeMatriculas,
                                   PublicadorDeEventos publicador) {
        this.repositorioDeMatriculas = repositorioDeMatriculas;
        this.publicador = publicador;
    }

    @Transactional
    public void executar(Long matriculaId) {
        Matricula matricula = repositorioDeMatriculas.porId(matriculaId)
                .orElseThrow(() -> new RegraDeNegocioException("Matricula nao encontrada"));
        repositorioDeMatriculas.excluir(matriculaId);
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.MATRICULA_EXCLUIDA,
                String.valueOf(matriculaId), "Matricula " + matricula.descricaoDoPlano()
                        + " do aluno #" + matricula.alunoId() + " excluida"));
    }
}

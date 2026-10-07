package school.cesar.gymflow.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.acesso.repositorio.RepositorioDeCredenciais;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDePresencas;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFichas;

/**
 * Caso de uso: excluir aluno (Integrante 2).
 * Regra: aluno com matricula ATIVA nao pode ser excluido. Se tiver apenas
 * matriculas canceladas/vencidas, o historico dele (matriculas e fichas) e removido junto,
 * para nao deixar registros orfaos no banco.
 */
@Service
public class ExcluirAlunoUseCase {

    private final RepositorioDeAlunos repositorioDeAlunos;
    private final RepositorioDeMatriculas repositorioDeMatriculas;
    private final RepositorioDeFichas repositorioDeFichas;
    private final RepositorioDePresencas repositorioDePresencas;
    private final RepositorioDeCredenciais repositorioDeCredenciais;
    private final PublicadorDeEventos publicador;

    public ExcluirAlunoUseCase(RepositorioDeAlunos repositorioDeAlunos,
                               RepositorioDeMatriculas repositorioDeMatriculas,
                               RepositorioDeFichas repositorioDeFichas,
                               RepositorioDePresencas repositorioDePresencas,
                               RepositorioDeCredenciais repositorioDeCredenciais,
                               PublicadorDeEventos publicador) {
        this.repositorioDeAlunos = repositorioDeAlunos;
        this.repositorioDeMatriculas = repositorioDeMatriculas;
        this.repositorioDeFichas = repositorioDeFichas;
        this.repositorioDePresencas = repositorioDePresencas;
        this.repositorioDeCredenciais = repositorioDeCredenciais;
        this.publicador = publicador;
    }

    @Transactional
    public void executar(Long alunoId) {
        Aluno aluno = repositorioDeAlunos.porId(alunoId)
                .orElseThrow(() -> new RegraDeNegocioException("Aluno nao encontrado"));
        List<Matricula> matriculas = repositorioDeMatriculas.porAluno(alunoId);
        matriculas.stream().filter(Matricula::ativa).findFirst().ifPresent(m -> {
            throw new RegraDeNegocioException("Nao e possivel excluir " + aluno.nome()
                    + ": ele possui matricula ativa (" + m.descricaoDoPlano()
                    + "). Cancele ou exclua a matricula antes.");
        });
        matriculas.forEach(m -> repositorioDeMatriculas.excluir(m.id()));
        repositorioDeFichas.excluirPorAluno(alunoId);
        repositorioDePresencas.excluirPorAluno(alunoId);
        repositorioDeCredenciais.excluirPorAluno(alunoId);
        repositorioDeAlunos.excluir(alunoId);
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.ALUNO_EXCLUIDO,
                String.valueOf(alunoId), "Aluno " + aluno.nome() + " excluido ("
                        + matriculas.size() + " matricula(s) antiga(s) removida(s))"));
    }
}

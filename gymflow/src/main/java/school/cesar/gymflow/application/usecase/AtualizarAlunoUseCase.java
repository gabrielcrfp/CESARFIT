package school.cesar.gymflow.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.Cpf;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Caso de uso: editar dados do aluno (complexidade media - Integrante 2). */
@Service
public class AtualizarAlunoUseCase {

    private final RepositorioDeAlunos repositorioDeAlunos;
    private final RepositorioDeMatriculas repositorioDeMatriculas;
    private final PublicadorDeEventos publicador;

    public AtualizarAlunoUseCase(RepositorioDeAlunos repositorioDeAlunos,
                                 RepositorioDeMatriculas repositorioDeMatriculas,
                                 PublicadorDeEventos publicador) {
        this.repositorioDeAlunos = repositorioDeAlunos;
        this.repositorioDeMatriculas = repositorioDeMatriculas;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.AlunoResumo executar(Long alunoId, Comandos.SalvarAluno comando) {
        if (comando == null) {
            throw new RegraDeNegocioException("Dados do aluno nao informados");
        }
        Aluno aluno = repositorioDeAlunos.porId(alunoId)
                .orElseThrow(() -> new RegraDeNegocioException("Aluno nao encontrado"));
        Cpf cpf = Cpf.de(comando.cpf());
        if (!aluno.cpf().equals(cpf)) {
            throw new RegraDeNegocioException("O CPF do aluno e imutavel; altere apenas os dados de contato");
        }
        aluno.atualizar(comando.nome(), aluno.cpf(), comando.telefone(), comando.email(),
                comando.dataDeNascimento(), comando.objetivo());
        Aluno salvo = repositorioDeAlunos.salvar(aluno);
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.ALUNO_ATUALIZADO,
                String.valueOf(salvo.id()), "Dados de " + salvo.nome() + " atualizados"));
        return Resumos.aluno(salvo, repositorioDeMatriculas.porAluno(alunoId));
    }
}

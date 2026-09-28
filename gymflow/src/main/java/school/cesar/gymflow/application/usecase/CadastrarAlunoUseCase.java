package school.cesar.gymflow.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.shared.Cpf;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Caso de uso: cadastrar aluno com validacoes (complexidade media - Integrante 1). */
@Service
public class CadastrarAlunoUseCase {

    private final RepositorioDeAlunos repositorioDeAlunos;
    private final PublicadorDeEventos publicador;

    public CadastrarAlunoUseCase(RepositorioDeAlunos repositorioDeAlunos, PublicadorDeEventos publicador) {
        this.repositorioDeAlunos = repositorioDeAlunos;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.AlunoResumo executar(Comandos.SalvarAluno comando) {
        if (comando == null) {
            throw new RegraDeNegocioException("Dados do aluno nao informados");
        }
        Cpf cpf = Cpf.de(comando.cpf());
        repositorioDeAlunos.porCpf(cpf).ifPresent(existente -> {
            throw new RegraDeNegocioException("Ja existe aluno cadastrado com o CPF " + cpf);
        });
        Aluno aluno = new Aluno(null, comando.nome(), cpf, comando.telefone(), comando.email(),
                comando.dataDeNascimento(), comando.objetivo());
        Aluno salvo = repositorioDeAlunos.salvar(aluno);
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.ALUNO_CADASTRADO,
                String.valueOf(salvo.id()), "Aluno " + salvo.nome() + " cadastrado"));
        return Resumos.aluno(salvo, List.of());
    }
}

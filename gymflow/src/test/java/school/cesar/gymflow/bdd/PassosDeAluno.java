package school.cesar.gymflow.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.usecase.AtualizarAlunoUseCase;
import school.cesar.gymflow.application.usecase.CadastrarAlunoUseCase;
import school.cesar.gymflow.application.usecase.ExcluirAlunoUseCase;
import school.cesar.gymflow.application.usecase.ServicoDeConsultas;
import school.cesar.gymflow.domain.matricula.model.Objetivo;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

public class PassosDeAluno {

    @Autowired
    private ContextoDeTeste contexto;

    @Autowired
    private PassosComuns comuns;

    @Autowired
    private CadastrarAlunoUseCase cadastrarAluno;

    @Autowired
    private AtualizarAlunoUseCase atualizarAluno;

    @Autowired
    private ExcluirAlunoUseCase excluirAluno;

    @Autowired
    private ServicoDeConsultas consultas;

    @Dado("um aluno chamado {string} com CPF {string}")
    public void umAluno(String nome, String cpf) {
        contexto.alunoId(comuns.cadastrar(nome, cpf, Objetivo.CONDICIONAMENTO));
    }

    @Quando("eu alterar o nome do aluno para {string} e o telefone para {string}")
    public void alterarAluno(String nome, String telefone) {
        var atual = consultas.aluno(contexto.alunoId()).aluno();
        atualizarAluno.executar(contexto.alunoId(), new Comandos.SalvarAluno(nome, atual.cpf(),
                telefone, atual.email(), atual.dataDeNascimento(), Objetivo.valueOf(atual.objetivo())));
    }

    @Quando("eu tentar cadastrar um aluno com CPF {string}")
    public void tentarCadastrarComCpf(String cpf) {
        try {
            comuns.cadastrar("Outro Aluno", cpf, Objetivo.HIPERTROFIA);
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    @Quando("eu tentar cadastrar um aluno sem telefone")
    public void cadastrarSemTelefone() {
        try {
            cadastrarAluno.executar(new Comandos.SalvarAluno("Sem Fone", "99988877766", "",
                    "semfone@email.com", LocalDate.of(1990, 1, 1), Objetivo.HIPERTROFIA));
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    @Quando("eu excluir o aluno")
    public void excluir() {
        excluirAluno.executar(contexto.alunoId());
    }

    @Quando("eu tentar excluir o aluno")
    public void tentarExcluir() {
        try {
            excluirAluno.executar(contexto.alunoId());
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    @Entao("o aluno deve se chamar {string} com telefone {string}")
    public void alunoDeveSer(String nome, String telefone) {
        var aluno = consultas.aluno(contexto.alunoId()).aluno();
        assertEquals(nome, aluno.nome());
        assertEquals(telefone, aluno.telefone());
    }

    @Entao("o aluno nao deve mais aparecer na listagem")
    public void naoApareceNaListagem() {
        assertTrue(consultas.alunos(null).stream().noneMatch(a -> a.id().equals(contexto.alunoId())));
    }

    @Entao("a pesquisa por {string} deve encontrar o aluno")
    public void pesquisaEncontra(String termo) {
        assertTrue(consultas.alunos(termo).stream().anyMatch(a -> a.id().equals(contexto.alunoId())));
    }
}

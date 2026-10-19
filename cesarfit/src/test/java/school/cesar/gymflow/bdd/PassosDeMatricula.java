package school.cesar.gymflow.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.usecase.AtualizarMatriculaUseCase;
import school.cesar.gymflow.application.usecase.ExcluirMatriculaUseCase;
import school.cesar.gymflow.application.usecase.MatricularAlunoUseCase;
import school.cesar.gymflow.application.usecase.RegistrarPagamentoUseCase;
import school.cesar.gymflow.application.usecase.ServicoDeConsultas;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

public class PassosDeMatricula {

    @Autowired
    private ContextoDeTeste contexto;

    @Autowired
    private MatricularAlunoUseCase matricular;

    @Autowired
    private AtualizarMatriculaUseCase atualizar;

    @Autowired
    private ExcluirMatriculaUseCase excluir;

    @Autowired
    private RegistrarPagamentoUseCase registrarPagamento;

    @Autowired
    private ServicoDeConsultas consultas;

    @Dado("que o aluno ja foi matriculado no plano {string}")
    public void jaMatriculado(String plano) {
        matricularNoPlano(plano);
    }

    @Quando("eu matricular o aluno no plano {string}")
    public void matricularNoPlano(String plano) {
        contexto.matricula(matricular.executar(
                new Comandos.SalvarMatricula(contexto.alunoId(), plano, LocalDate.now(), null)));
    }

    @Quando("eu tentar matricular o aluno no plano {string}")
    public void tentarMatricular(String plano) {
        try {
            matricularNoPlano(plano);
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    @Quando("eu alterar o plano da matricula para {string}")
    public void alterarPlano(String plano) {
        alterar(plano, "ATIVA");
    }

    @Quando("eu alterar a situacao da matricula para {string}")
    public void alterarSituacao(String status) {
        alterar(contexto.matricula().plano(), status);
    }

    private void alterar(String plano, String status) {
        var atual = contexto.matricula();
        contexto.matricula(atualizar.executar(atual.id(),
                new Comandos.SalvarMatricula(atual.alunoId(), plano, atual.inicio(), status)));
    }

    @Quando("eu excluir a matricula")
    public void excluirMatricula() {
        excluir.executar(contexto.matricula().id());
    }

    @Quando("eu registrar um pagamento de {string} via {string}")
    public void registrarPagamento(String valor, String forma) {
        try {
            contexto.matricula(registrarPagamento.executar(new Comandos.RegistrarPagamento(
                    contexto.matricula().id(), valor, forma)));
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    @Entao("o valor da matricula deve ser {string}")
    public void valorDeveSer(String valor) {
        assertEquals(valor, contexto.matricula().valorMensal());
    }

    @Entao("o valor salvo no banco deve ser {string}")
    public void valorSalvo(String valor) {
        assertEquals(valor, consultas.matricula(contexto.matricula().id()).valorMensal());
    }

    @Entao("o status da matricula deve ser {string}")
    public void statusDeveSer(String status) {
        assertEquals(status, consultas.matricula(contexto.matricula().id()).status());
    }

    @Entao("a matricula nao deve mais aparecer na listagem")
    public void naoAparece() {
        Long id = contexto.matricula().id();
        assertTrue(consultas.matriculas(null, null, null, null).stream().noneMatch(m -> m.id().equals(id)));
    }

    @Entao("o dashboard deve contar a matricula como ativa")
    public void dashboardConta() {
        assertTrue(consultas.painel().matriculasAtivas() >= 1);
    }

    @Entao("o filtro por plano {string} deve encontrar a matricula")
    public void filtroEncontra(String plano) {
        Long id = contexto.matricula().id();
        assertTrue(consultas.matriculas(null, null, plano, null).stream().anyMatch(m -> m.id().equals(id)));
    }
}

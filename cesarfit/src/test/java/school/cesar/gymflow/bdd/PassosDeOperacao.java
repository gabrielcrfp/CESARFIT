package school.cesar.gymflow.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.After;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.application.usecase.MatricularAlunoUseCase;
import school.cesar.gymflow.application.usecase.NotificacaoDeVencimentoUseCase;
import school.cesar.gymflow.application.usecase.OcorrenciaDeEquipamentoUseCase;

public class PassosDeOperacao {

    @Autowired
    private ContextoDeTeste contexto;

    @Autowired
    private MatricularAlunoUseCase matricular;

    @Autowired
    private NotificacaoDeVencimentoUseCase notificacoes;

    @Autowired
    private OcorrenciaDeEquipamentoUseCase ocorrencias;

    private Respostas.OcorrenciaResumo ocorrencia;

    private void capturar(Runnable acao) {
        try {
            acao.run();
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    @Dado("que o aluno tem uma matricula {string} iniciada ha {int} dias")
    public void matriculaIniciadaHa(String plano, int dias) {
        contexto.matricula(matricular.executar(new Comandos.SalvarMatricula(
                contexto.alunoId(), plano, LocalDate.now().minusDays(dias), null)));
    }

    @Quando("o sistema verificar os vencimentos")
    public void verificar() {
        notificacoes.verificar(LocalDate.now());
    }

    @Entao("o aluno deve ter {int} aviso de vencimento")
    public void avisos(int quantidade) {
        assertEquals(quantidade, notificacoes.doAluno(contexto.alunoId()).size());
    }

    @Quando("eu registrar uma ocorrencia para o equipamento {string} com a descricao {string}")
    public void registrar(String equipamento, String descricao) {
        ocorrencia = ocorrencias.registrar(new Comandos.RegistrarOcorrencia(equipamento, descricao));
    }

    @Quando("eu tentar registrar uma ocorrencia para o equipamento {string} com a descricao {string}")
    public void tentarRegistrar(String equipamento, String descricao) {
        capturar(() -> ocorrencias.registrar(new Comandos.RegistrarOcorrencia(equipamento, descricao)));
    }

    @Quando("eu colocar a ocorrencia em andamento")
    public void iniciar() {
        ocorrencia = ocorrencias.iniciar(ocorrencia.id());
    }

    @Quando("eu resolver a ocorrencia com a solucao {string}")
    public void resolver(String solucao) {
        ocorrencia = ocorrencias.resolver(ocorrencia.id(), solucao);
    }

    @Quando("eu tentar resolver a ocorrencia com a solucao {string}")
    public void tentarResolver(String solucao) {
        capturar(() -> resolver(solucao));
    }

    @Entao("a ocorrencia deve estar {string}")
    public void ocorrenciaDeveEstar(String status) {
        assertEquals(status, ocorrencia.status());
    }
}

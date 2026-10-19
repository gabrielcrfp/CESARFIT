package school.cesar.gymflow.bdd;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.usecase.ConcluirItemDeTreinoUseCase;
import school.cesar.gymflow.application.usecase.ConsultarProximoExercicioUseCase;
import school.cesar.gymflow.application.usecase.GerarFichaDeTreinoUseCase;
import school.cesar.gymflow.application.usecase.MatricularAlunoUseCase;
import school.cesar.gymflow.domain.matricula.model.Objetivo;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PassosDeTreino {

    @Autowired
    private ContextoDeTeste contexto;

    @Autowired
    private PassosComuns comuns;

    @Autowired
    private MatricularAlunoUseCase matricular;

    @Autowired
    private GerarFichaDeTreinoUseCase gerarFicha;

    @Autowired
    private ConcluirItemDeTreinoUseCase concluirItem;

    @Autowired
    private ConsultarProximoExercicioUseCase proximoExercicio;

    @Dado("um aluno matriculado chamado {string} com CPF {string} e objetivo {string}")
    public void alunoMatriculadoComObjetivo(String nome, String cpf, String objetivo) {
        Long alunoId = comuns.cadastrar(nome, cpf, Objetivo.valueOf(objetivo));
        contexto.alunoId(alunoId);
        contexto.matricula(matricular.executar(new Comandos.SalvarMatricula(
                alunoId, "MENSAL", LocalDate.now(), null)));
    }

    @Dado("um aluno sem matricula chamado {string} com CPF {string}")
    public void alunoSemMatricula(String nome, String cpf) {
        contexto.alunoId(comuns.cadastrar(nome, cpf, Objetivo.HIPERTROFIA));
    }

    @Dado("que a ficha de treino do aluno foi gerada")
    public void fichaJaGerada() {
        contexto.ficha(gerarFicha.executar(contexto.alunoId()));
    }

    @Quando("eu gerar a ficha de treino do aluno")
    public void gerarFichaDoAluno() {
        contexto.ficha(gerarFicha.executar(contexto.alunoId()));
    }

    @Quando("eu tentar gerar a ficha de treino do aluno")
    public void tentarGerarFicha() {
        try {
            contexto.ficha(gerarFicha.executar(contexto.alunoId()));
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    @Quando("eu concluir o item na posicao {int}")
    public void concluirItemNaPosicao(int posicao) {
        contexto.ficha(concluirItem.executar(
                new Comandos.ConcluirItem(contexto.ficha().id(), posicao)));
    }

    @Entao("a ficha deve ter {int} itens pendentes")
    public void fichaDeveTerPendentes(int pendentes) {
        assertEquals(pendentes, contexto.ficha().pendentes());
    }

    @Entao("o proximo exercicio pendente deve ser {string}")
    public void proximoExercicioDeveSer(String exercicio) {
        var proximo = proximoExercicio.executar(contexto.ficha().id());
        assertTrue(proximo.isPresent(), "Nao ha exercicio pendente na ficha");
        assertEquals(exercicio, proximo.get().exercicio());
    }

    @Entao("devo receber o erro de treino {string}")
    public void devoReceberErroDeTreino(String mensagem) {
        assertNotNull(contexto.erro(), "Nenhum erro foi capturado no cenario");
        assertEquals(mensagem, contexto.erro().getMessage());
    }
}

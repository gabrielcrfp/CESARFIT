package school.cesar.gymflow.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.Before;
import io.cucumber.java.pt.Entao;
import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.usecase.CadastrarAlunoUseCase;
import school.cesar.gymflow.domain.matricula.model.Objetivo;

/** Steps reutilizados por mais de uma funcionalidade. */
public class PassosComuns {

    @Autowired
    private ContextoDeTeste contexto;

    @Autowired
    private CadastrarAlunoUseCase cadastrarAluno;

    @Before
    public void prepararCenario() {
        contexto.limpar();
    }

    Long cadastrar(String nome, String cpf, Objetivo objetivo) {
        var resumo = cadastrarAluno.executar(new Comandos.SalvarAluno(
                nome, cpf, "81988887777", nome.toLowerCase().replace(" ", ".") + "@email.com",
                LocalDate.of(1995, 5, 10), objetivo));
        assertNotNull(resumo.id());
        return resumo.id();
    }

    @Entao("devo receber o erro {string}")
    public void devoReceberOErro(String mensagem) {
        assertNotNull(contexto.erro(), "Nenhum erro foi capturado no cenario");
        assertEquals(mensagem, contexto.erro().getMessage());
    }
}

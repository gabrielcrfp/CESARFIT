package school.cesar.gymflow.bdd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.application.usecase.AlterarSenhaUseCase;
import school.cesar.gymflow.application.usecase.ConsultarAreaDoAlunoUseCase;
import school.cesar.gymflow.application.usecase.EntrarNoSistemaUseCase;
import school.cesar.gymflow.application.usecase.RegistrarPresencaUseCase;
import school.cesar.gymflow.domain.acesso.model.Credencial;
import school.cesar.gymflow.domain.acesso.repositorio.RepositorioDeCredenciais;
import school.cesar.gymflow.domain.acesso.servico.CodificadorDeSenha;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

public class PassosDeAcesso {

    @Autowired
    private ContextoDeTeste contexto;

    @Autowired
    private EntrarNoSistemaUseCase entrar;

    @Autowired
    private AlterarSenhaUseCase alterarSenha;

    @Autowired
    private RegistrarPresencaUseCase registrarPresenca;

    @Autowired
    private ConsultarAreaDoAlunoUseCase consultarArea;

    @Autowired
    private RepositorioDeCredenciais credenciais;

    @Autowired
    private CodificadorDeSenha codificador;

    private Respostas.Sessao sessao;

    @Dado("um membro da equipe com numero {string} e senha {string}")
    public void membroDaEquipe(String numero, String senha) {
        if (credenciais.daEquipe(numero).isEmpty()) {
            credenciais.salvar(Credencial.daEquipe(numero, "Equipe de teste", codificador.codificar(senha)));
        }
    }

    @Quando("o aluno entrar com o numero da matricula e a senha {string}")
    public void alunoEntrar(String senha) {
        tentarEntrar(String.valueOf(contexto.matricula().id()), senha);
    }

    @Quando("alguem entrar com o numero {string} e a senha {string}")
    public void alguemEntrar(String numero, String senha) {
        tentarEntrar(numero, senha);
    }

    @Quando("o aluno trocar a senha {string} pela nova senha {string}")
    public void trocarSenha(String atual, String nova) {
        alterarSenha.executar(contexto.alunoId(), new Comandos.AlterarSenha(atual, nova));
    }

    @Quando("o aluno fizer o check-in de hoje")
    @E("que o aluno ja fez o check-in de hoje")
    public void fazerCheckIn() {
        registrarPresenca.executar(contexto.alunoId());
    }

    @Quando("o aluno tentar fazer o check-in de hoje novamente")
    public void tentarCheckInNovamente() {
        try {
            registrarPresenca.executar(contexto.alunoId());
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    @Entao("o acesso deve ser liberado com o perfil {string}")
    public void acessoLiberado(String perfil) {
        assertNotNull(sessao, "Login nao foi liberado: " + contexto.erro());
        assertNotNull(sessao.token());
        assertEquals(perfil, sessao.perfil());
    }

    @Entao("a sequencia de dias seguidos deve ser {int}")
    public void sequenciaDeveSer(int dias) {
        assertEquals(dias, consultarArea.executar(contexto.alunoId()).frequencia().sequenciaAtual());
    }

    @E("a presenca de hoje deve aparecer na semana")
    public void presencaNaSemana() {
        var frequencia = consultarArea.executar(contexto.alunoId()).frequencia();
        assertTrue(frequencia.presenteHoje());
        assertTrue(frequencia.semana().stream().anyMatch(d -> d.hoje() && d.presente()));
    }

    private void tentarEntrar(String numero, String senha) {
        try {
            sessao = entrar.executar(new Comandos.Entrar(numero, senha));
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }
}

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
import school.cesar.gymflow.application.usecase.PersonalizarFichaUseCase;
import school.cesar.gymflow.application.usecase.ServicoDeConsultas;

public class PassosDePersonalizacao {

    @Autowired
    private ContextoDeTeste contexto;

    @Autowired
    private PersonalizarFichaUseCase personalizar;

    @Autowired
    private ServicoDeConsultas consultas;

    private void capturar(Runnable acao) {
        try {
            acao.run();
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    private Respostas.FichaResumo fichaAtual() {
        return consultas.fichasDoAluno(contexto.alunoId()).get(0);
    }

    private int posicao(String exercicio) {
        return fichaAtual().itens().stream().filter(i -> i.exercicio().equalsIgnoreCase(exercicio))
                .findFirst().orElseThrow(() -> new AssertionError("Exercicio fora da ficha: " + exercicio)).posicao();
    }

    private Long exercicioDoCatalogo(String nome) {
        return personalizar.catalogoAtivo().stream().filter(e -> e.nome().equalsIgnoreCase(nome)).findFirst()
                .orElseThrow(() -> new AssertionError("Exercicio fora do catalogo: " + nome)).id();
    }

    @Entao("a ficha atual do aluno deve ter os treinos A, B e C")
    public void temDivisaoABC() {
        var divisoes = fichaAtual().itens().stream().map(Respostas.ItemResumo::divisao).distinct().sorted().toList();
        assertEquals(List.of("A", "B", "C"), divisoes);
    }

    @Quando("o aluno incluir o exercicio {string} no treino {string} com {int} series de {int} repeticoes")
    public void incluir(String nome, String divisao, int series, int repeticoes) {
        personalizar.adicionar(contexto.alunoId(),
                new Comandos.AdicionarExercicioNaFicha(exercicioDoCatalogo(nome), divisao, series, repeticoes));
    }

    @Quando("o aluno tentar incluir o exercicio {string} no treino {string} com {int} series de {int} repeticoes")
    public void tentarIncluir(String nome, String divisao, int series, int repeticoes) {
        capturar(() -> incluir(nome, divisao, series, repeticoes));
    }

    @Quando("o aluno ajustar o exercicio {string} para {int} series de {int} repeticoes")
    public void ajustar(String nome, int series, int repeticoes) {
        personalizar.ajustar(contexto.alunoId(), posicao(nome), new Comandos.AjustarItemDaFicha(series, repeticoes));
    }

    @Quando("o aluno remover o exercicio {string}")
    public void remover(String nome) {
        personalizar.remover(contexto.alunoId(), posicao(nome));
    }

    @Quando("o aluno tentar remover o exercicio {string}")
    public void tentarRemover(String nome) {
        capturar(() -> remover(nome));
    }

    @Entao("o treino {string} deve conter {string} com {int} series de {int} repeticoes")
    public void treinoContem(String divisao, String nome, int series, int repeticoes) {
        assertTrue(fichaAtual().itens().stream().anyMatch(i -> i.divisao().equals(divisao)
                && i.exercicio().equalsIgnoreCase(nome) && i.series() == series && i.repeticoes() == repeticoes));
    }
}

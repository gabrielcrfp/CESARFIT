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
import school.cesar.gymflow.application.usecase.CatalogoDeExerciciosUseCase;

public class PassosDeCatalogo {

    @Autowired
    private ContextoDeTeste contexto;

    @Autowired
    private CatalogoDeExerciciosUseCase catalogo;

    /** Exercicios desativados no cenario voltam a ficar ativos para nao afetar os outros cenarios. */
    private final List<Long> desativados = new ArrayList<>();

    @After
    public void reativarCatalogo() {
        desativados.forEach(id -> {
            try {
                catalogo.reativar(id);
            } catch (RegraDeNegocioException ignorado) {
                // ja estava ativo
            }
        });
    }

    private Respostas.ExercicioResumo porNome(String nome) {
        return catalogo.listar().stream().filter(e -> e.nome().equalsIgnoreCase(nome)).findFirst()
                .orElseThrow(() -> new AssertionError("Exercicio nao esta no catalogo: " + nome));
    }

    @Quando("eu cadastrar o exercicio {string} do grupo {string} com equipamento {string}")
    public void cadastrar(String nome, String grupo, String equipamento) {
        catalogo.incluir(new Comandos.SalvarExercicio(nome, grupo, equipamento));
    }

    @Quando("eu tentar cadastrar o exercicio {string} do grupo {string} com equipamento {string}")
    public void tentarCadastrar(String nome, String grupo, String equipamento) {
        try {
            catalogo.incluir(new Comandos.SalvarExercicio(nome, grupo, equipamento));
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    @Dado("que o exercicio {string} foi desativado")
    public void desativado(String nome) {
        Long id = porNome(nome).id();
        catalogo.desativar(id);
        desativados.add(id);
    }

    @Quando("eu tentar desativar o exercicio {string}")
    public void tentarDesativar(String nome) {
        try {
            Long id = porNome(nome).id();
            catalogo.desativar(id);
            desativados.add(id);
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    @Quando("eu tentar excluir o exercicio {string}")
    public void tentarExcluir(String nome) {
        try {
            catalogo.excluir(porNome(nome).id());
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    @Entao("o exercicio {string} deve estar ativo no catalogo")
    public void ativoNoCatalogo(String nome) {
        assertTrue(porNome(nome).ativo());
    }

    @Entao("a ficha nao deve conter o exercicio {string}")
    public void fichaNaoContem(String nome) {
        assertFalse(contexto.ficha().itens().stream().anyMatch(i -> i.exercicio().equalsIgnoreCase(nome)));
    }

    @E("a ficha deve conter o exercicio {string}")
    public void fichaContem(String nome) {
        assertTrue(contexto.ficha().itens().stream().anyMatch(i -> i.exercicio().equalsIgnoreCase(nome)));
    }
}

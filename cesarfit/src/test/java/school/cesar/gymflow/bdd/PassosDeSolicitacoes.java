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
import school.cesar.gymflow.application.usecase.AtendimentoDoInstrutorUseCase;
import school.cesar.gymflow.application.usecase.AvaliacaoFisicaUseCase;
import school.cesar.gymflow.application.usecase.CatalogoDeExerciciosUseCase;
import school.cesar.gymflow.application.usecase.FeedbackPosTreinoUseCase;
import school.cesar.gymflow.application.usecase.ServicoDeConsultas;
import school.cesar.gymflow.application.usecase.SubstituicaoDeExercicioUseCase;

public class PassosDeSolicitacoes {

    @Autowired
    private ContextoDeTeste contexto;

    @Autowired
    private AvaliacaoFisicaUseCase avaliacoes;

    @Autowired
    private SubstituicaoDeExercicioUseCase substituicoes;

    @Autowired
    private AtendimentoDoInstrutorUseCase atendimentos;

    @Autowired
    private FeedbackPosTreinoUseCase feedbacks;

    @Autowired
    private CatalogoDeExerciciosUseCase catalogo;

    @Autowired
    private ServicoDeConsultas consultas;

    private Respostas.AvaliacaoResumo avaliacao;
    private Respostas.SubstituicaoResumo substituicao;
    private Respostas.AtendimentoResumo atendimento;

    private void capturar(Runnable acao) {
        try {
            acao.run();
        } catch (RegraDeNegocioException e) {
            contexto.erro(e);
        }
    }

    private static java.time.LocalDateTime daqui(int dias, int hora) {
        return LocalDate.now().plusDays(dias).atTime(hora, 0);
    }

    /* ---------- avaliacao fisica ---------- */

    @Quando("o aluno solicitar avaliacao fisica para daqui a {int} dias as {int}h")
    public void solicitarAvaliacao(int dias, int hora) {
        avaliacao = avaliacoes.solicitar(contexto.alunoId(), new Comandos.SolicitarAvaliacao(daqui(dias, hora)));
    }

    @Quando("o aluno tentar solicitar avaliacao fisica para daqui a {int} dias as {int}h")
    public void tentarSolicitarAvaliacao(int dias, int hora) {
        capturar(() -> solicitarAvaliacao(dias, hora));
    }

    @Quando("o aluno tentar solicitar avaliacao fisica para daqui a {int} dias as {int}:{int}")
    public void tentarSolicitarHorarioQuebrado(int dias, int hora, int minuto) {
        capturar(() -> avaliacao = avaliacoes.solicitar(contexto.alunoId(), new Comandos.SolicitarAvaliacao(
                LocalDate.now().plusDays(dias).atTime(hora, minuto))));
    }

    @Quando("a equipe propor um novo horario para daqui a {int} dias as {int}h")
    public void proporHorario(int dias, int hora) {
        avaliacao = avaliacoes.proporHorario(avaliacao.id(),
                new Comandos.ResponderAvaliacao(daqui(dias, hora), "Agenda do avaliador cheia"));
    }

    @Quando("o aluno aceitar a proposta")
    public void aceitar() {
        avaliacao = avaliacoes.aceitarProposta(contexto.alunoId(), avaliacao.id());
    }

    @Entao("a avaliacao deve estar {string}")
    public void avaliacaoDeveEstar(String status) {
        assertEquals(status, avaliacao.status());
        assertNotNull(avaliacao.dataAgendada());
    }

    /* ---------- substituicao ---------- */

    @Quando("o aluno pedir a substituicao do exercicio na posicao {int} com o motivo {string}")
    public void pedirSubstituicao(int posicao, String motivo) {
        substituicao = substituicoes.solicitar(contexto.alunoId(),
                new Comandos.SolicitarSubstituicao(contexto.ficha().id(), posicao, motivo));
    }

    private Long exercicio(String nome) {
        return catalogo.listar().stream().filter(e -> e.nome().equalsIgnoreCase(nome)).findFirst()
                .orElseThrow(() -> new AssertionError("Exercicio nao esta no catalogo: " + nome)).id();
    }

    @Quando("a equipe aprovar a substituicao pelo exercicio {string}")
    public void aprovar(String nome) {
        substituicao = substituicoes.aprovar(substituicao.id(), new Comandos.AprovarSubstituicao(exercicio(nome), null));
    }

    @Quando("a equipe tentar aprovar a substituicao pelo exercicio {string}")
    public void tentarAprovar(String nome) {
        capturar(() -> aprovar(nome));
    }

    @Quando("a equipe rejeitar a substituicao com a justificativa {string}")
    public void rejeitar(String justificativa) {
        substituicao = substituicoes.rejeitar(substituicao.id(), justificativa);
    }

    @Entao("a substituicao deve estar {string}")
    public void substituicaoDeveEstar(String status) {
        assertEquals(status, substituicao.status());
    }

    @Entao("o exercicio na posicao {int} da ficha deve ser {string}")
    public void exercicioNaPosicao(int posicao, String nome) {
        var ficha = consultas.fichasDoAluno(contexto.alunoId()).get(0);
        assertEquals(nome, ficha.itens().get(posicao).exercicio());
    }

    /* ---------- atendimento ---------- */

    @Quando("o aluno pedir atendimento do instrutor com o motivo {string}")
    public void pedirAtendimento(String motivo) {
        atendimento = atendimentos.abrir(contexto.alunoId(), new Comandos.AbrirAtendimento(null, motivo));
    }

    @Quando("o instrutor {string} assumir o atendimento")
    public void assumir(String instrutor) {
        atendimento = atendimentos.assumir(atendimento.id(), instrutor);
    }

    @Quando("o instrutor concluir o atendimento com a orientacao {string}")
    public void concluir(String orientacao) {
        atendimento = atendimentos.concluir(atendimento.id(), orientacao);
    }

    @Quando("o instrutor tentar concluir o atendimento com a orientacao {string}")
    public void tentarConcluir(String orientacao) {
        capturar(() -> concluir(orientacao));
    }

    @Entao("o atendimento deve estar {string}")
    public void atendimentoDeveEstar(String status) {
        assertEquals(status, atendimento.status());
    }

    /* ---------- feedback ---------- */

    @Quando("o aluno registrar o feedback com esforco {int} e a observacao {string}")
    public void registrarFeedback(int esforco, String observacao) {
        feedbacks.registrar(contexto.alunoId(), new Comandos.RegistrarFeedback(esforco, observacao));
    }

    @Quando("o aluno tentar registrar o feedback com esforco {int} e a observacao {string}")
    public void tentarRegistrarFeedback(int esforco, String observacao) {
        capturar(() -> registrarFeedback(esforco, observacao));
    }

    @Entao("o aluno deve ter {int} feedback registrado")
    public void feedbacksRegistrados(int quantidade) {
        assertEquals(quantidade, feedbacks.doAluno(contexto.alunoId()).size());
    }
}

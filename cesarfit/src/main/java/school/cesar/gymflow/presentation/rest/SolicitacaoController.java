package school.cesar.gymflow.presentation.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.application.usecase.AtendimentoDoInstrutorUseCase;
import school.cesar.gymflow.application.usecase.AvaliacaoFisicaUseCase;
import school.cesar.gymflow.application.usecase.ContestacaoDeCobrancaUseCase;
import school.cesar.gymflow.application.usecase.EstornarPagamentoUseCase;
import school.cesar.gymflow.application.usecase.FeedbackPosTreinoUseCase;
import school.cesar.gymflow.application.usecase.GerenciadorDeSessoes.SessaoAtiva;
import school.cesar.gymflow.application.usecase.SubstituicaoDeExercicioUseCase;

/** Respostas da equipe as solicitacoes dos alunos e acoes financeiras (estorno). */
@RestController
@RequestMapping("/api")
public class SolicitacaoController {

    private final AvaliacaoFisicaUseCase avaliacoes;
    private final SubstituicaoDeExercicioUseCase substituicoes;
    private final AtendimentoDoInstrutorUseCase atendimentos;
    private final ContestacaoDeCobrancaUseCase contestacoes;
    private final EstornarPagamentoUseCase estornar;
    private final FeedbackPosTreinoUseCase feedbacks;

    public SolicitacaoController(AvaliacaoFisicaUseCase avaliacoes, SubstituicaoDeExercicioUseCase substituicoes,
                                 AtendimentoDoInstrutorUseCase atendimentos, ContestacaoDeCobrancaUseCase contestacoes,
                                 EstornarPagamentoUseCase estornar, FeedbackPosTreinoUseCase feedbacks) {
        this.avaliacoes = avaliacoes;
        this.substituicoes = substituicoes;
        this.atendimentos = atendimentos;
        this.contestacoes = contestacoes;
        this.estornar = estornar;
        this.feedbacks = feedbacks;
    }

    private static String texto(Comandos.Texto corpo) {
        return corpo == null ? null : corpo.texto();
    }

    /* ---------- financeiro ---------- */

    @PostMapping("/matriculas/{id}/pagamentos/{indice}/estorno")
    public Respostas.MatriculaResumo estornar(@PathVariable Long id, @PathVariable int indice,
                                              @RequestBody Comandos.Texto corpo) {
        return estornar.executar(id, indice, texto(corpo));
    }

    @GetMapping("/contestacoes")
    public List<Respostas.ContestacaoResumo> contestacoes() {
        return contestacoes.todas();
    }

    @PostMapping("/contestacoes/{id}/analise")
    public Respostas.ContestacaoResumo analisar(@PathVariable Long id,
                                                @RequestBody Comandos.AnalisarContestacao comando) {
        return contestacoes.analisar(id, comando);
    }

    /* ---------- avaliacao fisica ---------- */

    @GetMapping("/avaliacoes")
    public List<Respostas.AvaliacaoResumo> avaliacoes() {
        return avaliacoes.todas();
    }

    @PostMapping("/avaliacoes/{id}/confirmacao")
    public Respostas.AvaliacaoResumo confirmar(@PathVariable Long id, @RequestBody(required = false) Comandos.Texto corpo) {
        return avaliacoes.confirmar(id, texto(corpo));
    }

    @PostMapping("/avaliacoes/{id}/proposta")
    public Respostas.AvaliacaoResumo propor(@PathVariable Long id, @RequestBody Comandos.ResponderAvaliacao comando) {
        return avaliacoes.proporHorario(id, comando);
    }

    /* ---------- substituicao ---------- */

    @GetMapping("/substituicoes")
    public List<Respostas.SubstituicaoResumo> substituicoes() {
        return substituicoes.todas();
    }

    @PostMapping("/substituicoes/{id}/aprovacao")
    public Respostas.SubstituicaoResumo aprovar(@PathVariable Long id, @RequestBody Comandos.AprovarSubstituicao comando) {
        return substituicoes.aprovar(id, comando);
    }

    @PostMapping("/substituicoes/{id}/rejeicao")
    public Respostas.SubstituicaoResumo rejeitar(@PathVariable Long id, @RequestBody Comandos.Texto corpo) {
        return substituicoes.rejeitar(id, texto(corpo));
    }

    /* ---------- atendimento ---------- */

    @GetMapping("/atendimentos")
    public List<Respostas.AtendimentoResumo> atendimentos() {
        return atendimentos.todos();
    }

    @PostMapping("/atendimentos/{id}/assuncao")
    public Respostas.AtendimentoResumo assumir(@PathVariable Long id,
                                               @RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva sessao) {
        return atendimentos.assumir(id, sessao.nome());
    }

    @PostMapping("/atendimentos/{id}/conclusao")
    public Respostas.AtendimentoResumo concluir(@PathVariable Long id, @RequestBody Comandos.Texto corpo) {
        return atendimentos.concluir(id, texto(corpo));
    }

    /* ---------- feedback (consulta da equipe) ---------- */

    @GetMapping("/alunos/{alunoId}/feedbacks")
    public List<Respostas.FeedbackResumo> feedbacks(@PathVariable Long alunoId) {
        return feedbacks.doAluno(alunoId);
    }
}

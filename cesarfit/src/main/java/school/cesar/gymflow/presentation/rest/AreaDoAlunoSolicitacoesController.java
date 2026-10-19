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
import school.cesar.gymflow.application.usecase.FeedbackPosTreinoUseCase;
import school.cesar.gymflow.application.usecase.GerenciadorDeSessoes.SessaoAtiva;
import school.cesar.gymflow.application.usecase.NotificacaoDeVencimentoUseCase;
import school.cesar.gymflow.application.usecase.PersonalizarFichaUseCase;
import school.cesar.gymflow.application.usecase.SolicitacoesDoAlunoUseCase;
import school.cesar.gymflow.application.usecase.SubstituicaoDeExercicioUseCase;

/** Solicitacoes feitas pelo aluno logado. Tudo usa o aluno da sessao, nunca um id enviado pela tela. */
@RestController
@RequestMapping("/api/area-aluno")
public class AreaDoAlunoSolicitacoesController {

    private final SolicitacoesDoAlunoUseCase consultar;
    private final NotificacaoDeVencimentoUseCase avisos;
    private final FeedbackPosTreinoUseCase feedbacks;
    private final AvaliacaoFisicaUseCase avaliacoes;
    private final SubstituicaoDeExercicioUseCase substituicoes;
    private final AtendimentoDoInstrutorUseCase atendimentos;
    private final ContestacaoDeCobrancaUseCase contestacoes;
    private final PersonalizarFichaUseCase personalizar;

    public AreaDoAlunoSolicitacoesController(SolicitacoesDoAlunoUseCase consultar, NotificacaoDeVencimentoUseCase avisos,
                                             FeedbackPosTreinoUseCase feedbacks, AvaliacaoFisicaUseCase avaliacoes,
                                             SubstituicaoDeExercicioUseCase substituicoes,
                                             AtendimentoDoInstrutorUseCase atendimentos,
                                             ContestacaoDeCobrancaUseCase contestacoes,
                                             PersonalizarFichaUseCase personalizar) {
        this.personalizar = personalizar;
        this.consultar = consultar;
        this.avisos = avisos;
        this.feedbacks = feedbacks;
        this.avaliacoes = avaliacoes;
        this.substituicoes = substituicoes;
        this.atendimentos = atendimentos;
        this.contestacoes = contestacoes;
    }

    @GetMapping("/solicitacoes")
    public Respostas.SolicitacoesDoAluno solicitacoes(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s) {
        return consultar.executar(s.alunoId());
    }

    @PostMapping("/avisos/{id}/leitura")
    public Respostas.NotificacaoResumo lerAviso(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s,
                                                @PathVariable Long id) {
        return avisos.marcarComoLida(s.alunoId(), id);
    }

    @PostMapping("/feedbacks")
    public ResponseEntity<Respostas.FeedbackResumo> feedback(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s,
                                                             @RequestBody Comandos.RegistrarFeedback comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedbacks.registrar(s.alunoId(), comando));
    }

    @PostMapping("/avaliacoes")
    public ResponseEntity<Respostas.AvaliacaoResumo> avaliacao(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s,
                                                               @RequestBody Comandos.SolicitarAvaliacao comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacoes.solicitar(s.alunoId(), comando));
    }

    @PostMapping("/avaliacoes/{id}/aceite")
    public Respostas.AvaliacaoResumo aceitar(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s,
                                             @PathVariable Long id) {
        return avaliacoes.aceitarProposta(s.alunoId(), id);
    }

    @PostMapping("/avaliacoes/{id}/cancelamento")
    public Respostas.AvaliacaoResumo cancelarAvaliacao(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s,
                                                       @PathVariable Long id) {
        return avaliacoes.cancelar(s.alunoId(), id);
    }

    @PostMapping("/substituicoes")
    public ResponseEntity<Respostas.SubstituicaoResumo> substituicao(
            @RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s,
            @RequestBody Comandos.SolicitarSubstituicao comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(substituicoes.solicitar(s.alunoId(), comando));
    }

    @PostMapping("/atendimentos")
    public ResponseEntity<Respostas.AtendimentoResumo> atendimento(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s,
                                                                   @RequestBody Comandos.AbrirAtendimento comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atendimentos.abrir(s.alunoId(), comando));
    }

    @PostMapping("/contestacoes")
    public ResponseEntity<Respostas.ContestacaoResumo> contestar(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s,
                                                                 @RequestBody Comandos.AbrirContestacao comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contestacoes.abrir(s.alunoId(), comando));
    }

    /* ---------- o aluno monta o proprio treino ---------- */

    @GetMapping("/catalogo")
    public List<Respostas.ExercicioResumo> catalogo() {
        return personalizar.catalogoAtivo();
    }

    @PostMapping("/ficha/itens")
    public Respostas.FichaResumo adicionarExercicio(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s,
                                                    @RequestBody Comandos.AdicionarExercicioNaFicha comando) {
        return personalizar.adicionar(s.alunoId(), comando);
    }

    @PutMapping("/ficha/itens/{posicao}")
    public Respostas.FichaResumo ajustarExercicio(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s,
                                                  @PathVariable int posicao,
                                                  @RequestBody Comandos.AjustarItemDaFicha comando) {
        return personalizar.ajustar(s.alunoId(), posicao, comando);
    }

    @DeleteMapping("/ficha/itens/{posicao}")
    public Respostas.FichaResumo removerExercicio(@RequestAttribute(FiltroDeSessao.ATRIBUTO) SessaoAtiva s,
                                                  @PathVariable int posicao) {
        return personalizar.remover(s.alunoId(), posicao);
    }
}

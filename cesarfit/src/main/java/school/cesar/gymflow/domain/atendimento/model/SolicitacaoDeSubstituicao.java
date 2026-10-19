package school.cesar.gymflow.domain.atendimento.model;

import java.time.LocalDateTime;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.shared.Texto;

/** Raiz de agregado: o aluno pede para trocar um exercicio da ficha; a equipe aprova ou rejeita. */
public class SolicitacaoDeSubstituicao {

    private Long id;
    private final Long alunoId;
    private final Long fichaId;
    private final int posicao;
    private final String exercicioAtual;
    private final String motivo;
    private StatusSolicitacao status;
    private String exercicioNovo;
    private String resposta;
    private final LocalDateTime criadaEm;
    private LocalDateTime respondidaEm;

    public SolicitacaoDeSubstituicao(Long id, Long alunoId, Long fichaId, int posicao, String exercicioAtual,
                                     String motivo, StatusSolicitacao status, String exercicioNovo,
                                     String resposta, LocalDateTime criadaEm, LocalDateTime respondidaEm) {
        this.id = id;
        this.alunoId = alunoId;
        this.fichaId = fichaId;
        this.posicao = posicao;
        this.exercicioAtual = exercicioAtual;
        this.motivo = Texto.obrigatorio(motivo, 10, 500, "Motivo da substituicao");
        this.status = status == null ? StatusSolicitacao.PENDENTE : status;
        this.exercicioNovo = exercicioNovo;
        this.resposta = resposta;
        this.criadaEm = criadaEm;
        this.respondidaEm = respondidaEm;
    }

    public static SolicitacaoDeSubstituicao solicitar(Long alunoId, Long fichaId, int posicao,
                                                      String exercicioAtual, String motivo) {
        return new SolicitacaoDeSubstituicao(null, alunoId, fichaId, posicao, exercicioAtual, motivo,
                StatusSolicitacao.PENDENTE, null, null, LocalDateTime.now(), null);
    }

    public void aprovar(String novoExercicio, String obs) {
        exigirPendente();
        exercicioNovo = novoExercicio;
        resposta = Texto.opcional(obs, 300, "Resposta");
        status = StatusSolicitacao.APROVADA;
        respondidaEm = LocalDateTime.now();
    }

    public void rejeitar(String justificativa) {
        exigirPendente();
        resposta = Texto.obrigatorio(justificativa, 10, 300, "Justificativa da rejeicao");
        status = StatusSolicitacao.REJEITADA;
        respondidaEm = LocalDateTime.now();
    }

    private void exigirPendente() {
        if (status != StatusSolicitacao.PENDENTE) {
            throw new RegraDeNegocioException("Solicitacao ja foi respondida");
        }
    }

    public boolean pendente() {
        return status == StatusSolicitacao.PENDENTE;
    }

    public void atribuirId(Long id) {
        this.id = id;
    }

    public Long id() { return id; }
    public Long alunoId() { return alunoId; }
    public Long fichaId() { return fichaId; }
    public int posicao() { return posicao; }
    public String exercicioAtual() { return exercicioAtual; }
    public String motivo() { return motivo; }
    public StatusSolicitacao status() { return status; }
    public String exercicioNovo() { return exercicioNovo; }
    public String resposta() { return resposta; }
    public LocalDateTime criadaEm() { return criadaEm; }
    public LocalDateTime respondidaEm() { return respondidaEm; }
}

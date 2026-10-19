package school.cesar.gymflow.domain.financeiro.model;

import java.time.LocalDateTime;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.shared.Texto;

/** Raiz de agregado: o aluno contesta um pagamento cobrado; a equipe analisa e registra o resultado. */
public class ContestacaoDeCobranca {

    private Long id;
    private final Long matriculaId;
    private final Long alunoId;
    private final int indiceDoPagamento;
    private final String valor;
    private final String motivo;
    private StatusContestacao status;
    private String parecer;
    private final LocalDateTime abertaEm;
    private LocalDateTime analisadaEm;

    public ContestacaoDeCobranca(Long id, Long matriculaId, Long alunoId, int indiceDoPagamento, String valor,
                                 String motivo, StatusContestacao status, String parecer,
                                 LocalDateTime abertaEm, LocalDateTime analisadaEm) {
        this.id = id;
        this.matriculaId = matriculaId;
        this.alunoId = alunoId;
        this.indiceDoPagamento = indiceDoPagamento;
        this.valor = valor;
        this.motivo = Texto.obrigatorio(motivo, 10, 500, "Motivo da contestacao");
        this.status = status == null ? StatusContestacao.ABERTA : status;
        this.parecer = parecer;
        this.abertaEm = abertaEm;
        this.analisadaEm = analisadaEm;
    }

    public static ContestacaoDeCobranca abrir(Long matriculaId, Long alunoId, int indice, String valor, String motivo) {
        return new ContestacaoDeCobranca(null, matriculaId, alunoId, indice, valor, motivo,
                StatusContestacao.ABERTA, null, LocalDateTime.now(), null);
    }

    public void analisar(boolean procedente, String parecerDaEquipe) {
        if (status != StatusContestacao.ABERTA) {
            throw new RegraDeNegocioException("Contestacao ja foi analisada");
        }
        parecer = Texto.obrigatorio(parecerDaEquipe, 10, 500, "Parecer da analise");
        status = procedente ? StatusContestacao.PROCEDENTE : StatusContestacao.IMPROCEDENTE;
        analisadaEm = LocalDateTime.now();
    }

    public boolean aberta() {
        return status == StatusContestacao.ABERTA;
    }

    public void atribuirId(Long id) {
        this.id = id;
    }

    public Long id() { return id; }
    public Long matriculaId() { return matriculaId; }
    public Long alunoId() { return alunoId; }
    public int indiceDoPagamento() { return indiceDoPagamento; }
    public String valor() { return valor; }
    public String motivo() { return motivo; }
    public StatusContestacao status() { return status; }
    public String parecer() { return parecer; }
    public LocalDateTime abertaEm() { return abertaEm; }
    public LocalDateTime analisadaEm() { return analisadaEm; }
}

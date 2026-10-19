package school.cesar.gymflow.domain.atendimento.model;

import java.time.LocalDateTime;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.shared.Texto;

/** Raiz de agregado: pedido de ajuda ao instrutor vinculado a ficha (e opcionalmente a um exercicio). */
public class SolicitacaoDeAtendimento {

    private Long id;
    private final Long alunoId;
    private final Long fichaId;
    private final String exercicio;
    private final String motivo;
    private StatusAtendimento status;
    private String instrutor;
    private String orientacao;
    private final LocalDateTime abertaEm;
    private LocalDateTime concluidaEm;

    public SolicitacaoDeAtendimento(Long id, Long alunoId, Long fichaId, String exercicio, String motivo,
                                    StatusAtendimento status, String instrutor, String orientacao,
                                    LocalDateTime abertaEm, LocalDateTime concluidaEm) {
        if (fichaId == null) {
            throw new RegraDeNegocioException("O atendimento deve estar vinculado a uma ficha de treino");
        }
        this.id = id;
        this.alunoId = alunoId;
        this.fichaId = fichaId;
        this.exercicio = exercicio;
        this.motivo = Texto.obrigatorio(motivo, 10, 500, "Motivo do atendimento");
        this.status = status == null ? StatusAtendimento.ABERTO : status;
        this.instrutor = instrutor;
        this.orientacao = orientacao;
        this.abertaEm = abertaEm;
        this.concluidaEm = concluidaEm;
    }

    public static SolicitacaoDeAtendimento abrir(Long alunoId, Long fichaId, String exercicio, String motivo) {
        return new SolicitacaoDeAtendimento(null, alunoId, fichaId, exercicio, motivo,
                StatusAtendimento.ABERTO, null, null, LocalDateTime.now(), null);
    }

    public void assumir(String nomeDoInstrutor) {
        if (status != StatusAtendimento.ABERTO) {
            throw new RegraDeNegocioException("Somente atendimento aberto pode ser assumido");
        }
        instrutor = Texto.obrigatorio(nomeDoInstrutor, 2, 80, "Instrutor");
        status = StatusAtendimento.EM_ATENDIMENTO;
    }

    public void concluir(String orientacaoDoInstrutor) {
        if (status != StatusAtendimento.EM_ATENDIMENTO) {
            throw new RegraDeNegocioException("O atendimento precisa ser assumido antes de ser concluido");
        }
        orientacao = Texto.obrigatorio(orientacaoDoInstrutor, 10, 500, "Orientacao do instrutor");
        status = StatusAtendimento.CONCLUIDO;
        concluidaEm = LocalDateTime.now();
    }

    public boolean emAberto() {
        return status != StatusAtendimento.CONCLUIDO;
    }

    public void atribuirId(Long id) {
        this.id = id;
    }

    public Long id() { return id; }
    public Long alunoId() { return alunoId; }
    public Long fichaId() { return fichaId; }
    public String exercicio() { return exercicio; }
    public String motivo() { return motivo; }
    public StatusAtendimento status() { return status; }
    public String instrutor() { return instrutor; }
    public String orientacao() { return orientacao; }
    public LocalDateTime abertaEm() { return abertaEm; }
    public LocalDateTime concluidaEm() { return concluidaEm; }
}

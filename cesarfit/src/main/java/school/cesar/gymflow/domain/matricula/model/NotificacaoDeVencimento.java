package school.cesar.gymflow.domain.matricula.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Aviso enviado ao aluno quando a matricula esta perto de vencer.
 * Uma notificacao por matricula e por data de vencimento (evita envio duplicado).
 */
public class NotificacaoDeVencimento {

    public static final int DIAS_DE_ANTECEDENCIA = 3;

    private Long id;
    private final Long matriculaId;
    private final Long alunoId;
    private final LocalDate vencimento;
    private final LocalDateTime enviadaEm;
    private final String mensagem;
    private boolean lida;

    public NotificacaoDeVencimento(Long id, Long matriculaId, Long alunoId, LocalDate vencimento,
                                   LocalDateTime enviadaEm, String mensagem, boolean lida) {
        this.id = id;
        this.matriculaId = matriculaId;
        this.alunoId = alunoId;
        this.vencimento = vencimento;
        this.enviadaEm = enviadaEm;
        this.mensagem = mensagem;
        this.lida = lida;
    }

    /** A matricula precisa estar ativa e vencer entre hoje e os proximos dias de antecedencia. */
    public static boolean deveAvisar(Matricula matricula, LocalDate hoje) {
        if (!matricula.ativa()) {
            return false;
        }
        long dias = ChronoUnit.DAYS.between(hoje, matricula.fim());
        return dias >= 0 && dias <= DIAS_DE_ANTECEDENCIA;
    }

    public static NotificacaoDeVencimento para(Matricula matricula, String nomeDoAluno, LocalDate hoje) {
        if (!deveAvisar(matricula, hoje)) {
            throw new RegraDeNegocioException("Matricula fora do periodo de aviso de vencimento");
        }
        long dias = ChronoUnit.DAYS.between(hoje, matricula.fim());
        String quando = dias == 0 ? "hoje" : dias == 1 ? "amanha" : "em " + dias + " dias";
        String mensagem = "Ola, " + nomeDoAluno + "! Sua matricula no " + matricula.descricaoDoPlano()
                + " vence " + quando + ". Procure a recepcao para renovar.";
        return new NotificacaoDeVencimento(null, matricula.id(), matricula.alunoId(), matricula.fim(),
                LocalDateTime.now(), mensagem, false);
    }

    public void marcarComoLida() {
        if (lida) {
            throw new RegraDeNegocioException("Aviso ja foi marcado como lido");
        }
        lida = true;
    }

    public void atribuirId(Long id) {
        this.id = id;
    }

    public Long id() { return id; }
    public Long matriculaId() { return matriculaId; }
    public Long alunoId() { return alunoId; }
    public LocalDate vencimento() { return vencimento; }
    public LocalDateTime enviadaEm() { return enviadaEm; }
    public String mensagem() { return mensagem; }
    public boolean lida() { return lida; }
}

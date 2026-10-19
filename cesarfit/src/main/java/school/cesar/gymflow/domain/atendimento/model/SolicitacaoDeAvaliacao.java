package school.cesar.gymflow.domain.atendimento.model;

import java.time.LocalDateTime;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.shared.Texto;

/**
 * Raiz de agregado: pedido de avaliacao fisica.
 * Aluno pede uma data; a equipe confirma ou propoe outro horario; o aluno aceita a proposta ou cancela.
 */
public class SolicitacaoDeAvaliacao {

    public static final int ABERTURA = 6;
    public static final int FECHAMENTO = 21;
    public static final int DIAS_MAXIMOS = 60;

    private Long id;
    private final Long alunoId;
    private final LocalDateTime dataDesejada;
    private LocalDateTime dataProposta;
    private LocalDateTime dataAgendada;
    private StatusAvaliacao status;
    private String observacao;
    private final LocalDateTime criadaEm;

    public SolicitacaoDeAvaliacao(Long id, Long alunoId, LocalDateTime dataDesejada, LocalDateTime dataProposta,
                                  LocalDateTime dataAgendada, StatusAvaliacao status, String observacao,
                                  LocalDateTime criadaEm) {
        this.id = id;
        this.alunoId = alunoId;
        this.dataDesejada = dataDesejada;
        this.dataProposta = dataProposta;
        this.dataAgendada = dataAgendada;
        this.status = status;
        this.observacao = observacao;
        this.criadaEm = criadaEm;
    }

    public static SolicitacaoDeAvaliacao solicitar(Long alunoId, LocalDateTime desejada) {
        validarHorario(desejada);
        return new SolicitacaoDeAvaliacao(null, alunoId, desejada, null, null,
                StatusAvaliacao.SOLICITADA, null, LocalDateTime.now());
    }

    static void validarHorario(LocalDateTime data) {
        LocalDateTime agora = LocalDateTime.now();
        if (data == null || !data.isAfter(agora)) {
            throw new RegraDeNegocioException("A data da avaliacao deve ser futura");
        }
        if (data.isAfter(agora.plusDays(DIAS_MAXIMOS))) {
            throw new RegraDeNegocioException("A avaliacao deve ser marcada em ate " + DIAS_MAXIMOS + " dias");
        }
        if (data.getHour() < ABERTURA || data.getHour() >= FECHAMENTO) {
            throw new RegraDeNegocioException("Horario fora do funcionamento da academia (6h as 21h)");
        }
        if ((data.getMinute() != 0 && data.getMinute() != 30) || data.getSecond() != 0) {
            throw new RegraDeNegocioException("Escolha um horario redondo (ex.: 14:00 ou 14:30)");
        }
    }

    public void confirmar(String obs) {
        if (status != StatusAvaliacao.SOLICITADA) {
            throw new RegraDeNegocioException("Somente solicitacao pendente pode ser confirmada");
        }
        dataAgendada = dataDesejada;
        observacao = Texto.opcional(obs, 300, "Observacao");
        status = StatusAvaliacao.CONFIRMADA;
    }

    public void proporNovoHorario(LocalDateTime novaData, String obs) {
        if (status != StatusAvaliacao.SOLICITADA) {
            throw new RegraDeNegocioException("Somente solicitacao pendente pode receber nova proposta");
        }
        validarHorario(novaData);
        if (novaData.equals(dataDesejada)) {
            throw new RegraDeNegocioException("O novo horario deve ser diferente do solicitado");
        }
        dataProposta = novaData;
        observacao = Texto.opcional(obs, 300, "Observacao");
        status = StatusAvaliacao.NOVO_HORARIO_PROPOSTO;
    }

    public void aceitarProposta() {
        if (status != StatusAvaliacao.NOVO_HORARIO_PROPOSTO) {
            throw new RegraDeNegocioException("Nao ha proposta de novo horario para aceitar");
        }
        if (!dataProposta.isAfter(LocalDateTime.now())) {
            throw new RegraDeNegocioException("O horario proposto ja passou; faca uma nova solicitacao");
        }
        dataAgendada = dataProposta;
        status = StatusAvaliacao.CONFIRMADA;
    }

    public void cancelar() {
        if (!emAberto()) {
            throw new RegraDeNegocioException("Esta solicitacao nao pode mais ser cancelada");
        }
        status = StatusAvaliacao.CANCELADA;
    }

    /** Em aberto = pendente, com proposta ou confirmada para o futuro. */
    public boolean emAberto() {
        return switch (status) {
            case SOLICITADA, NOVO_HORARIO_PROPOSTO -> true;
            case CONFIRMADA -> dataAgendada != null && dataAgendada.isAfter(LocalDateTime.now());
            case CANCELADA -> false;
        };
    }

    public void atribuirId(Long id) {
        this.id = id;
    }

    public Long id() { return id; }
    public Long alunoId() { return alunoId; }
    public LocalDateTime dataDesejada() { return dataDesejada; }
    public LocalDateTime dataProposta() { return dataProposta; }
    public LocalDateTime dataAgendada() { return dataAgendada; }
    public StatusAvaliacao status() { return status; }
    public String observacao() { return observacao; }
    public LocalDateTime criadaEm() { return criadaEm; }
}

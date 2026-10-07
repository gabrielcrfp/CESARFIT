package school.cesar.gymflow.domain.matricula.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import school.cesar.gymflow.domain.shared.Dinheiro;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Raiz de agregado: Matricula.
 * Associa um aluno a um plano (Semanal ou Mensal). O valor e SEMPRE recebido ja
 * calculado pela politica de preco do plano, tanto na abertura quanto na edicao,
 * e fica salvo junto da matricula.
 */
public class Matricula {

    private Long id;
    private Long alunoId;
    private Modalidade modalidade;
    private Dinheiro valorMensal;
    private LocalDate inicio;
    private LocalDate fim;
    private StatusMatricula status;
    private String justificativaCancelamento;
    private LocalDate canceladaEm;
    private final List<Pagamento> pagamentos = new ArrayList<>();

    public Matricula(Long id, Long alunoId, Modalidade modalidade, Dinheiro valorMensal,
                     LocalDate inicio, LocalDate fim, StatusMatricula status,
                     List<Pagamento> pagamentos) {
        this.id = id;
        this.alunoId = alunoId;
        this.modalidade = modalidade;
        this.valorMensal = valorMensal;
        this.inicio = inicio;
        this.fim = fim;
        this.status = status;
        if (pagamentos != null) {
            this.pagamentos.addAll(pagamentos);
        }
    }

    /** Fabrica de agregado. */
    public static Matricula abrir(Long alunoId, Modalidade modalidade, Dinheiro valor, LocalDate inicio) {
        validar(alunoId, modalidade, valor, inicio);
        return new Matricula(null, alunoId, modalidade, valor, inicio,
                modalidade.fimAPartirDe(inicio), StatusMatricula.ATIVA, List.of());
    }

    /**
     * Edicao da matricula. Ao trocar o plano, o valor recalculado e a nova
     * vigencia passam a valer imediatamente e sao persistidos.
     */
    public void alterar(Long novoAlunoId, Modalidade novaModalidade, Dinheiro novoValor,
                        LocalDate novoInicio, StatusMatricula novoStatus) {
        validar(novoAlunoId, novaModalidade, novoValor, novoInicio);
        if (novoStatus == null) {
            throw new RegraDeNegocioException("Situacao da matricula e obrigatoria");
        }
        this.alunoId = novoAlunoId;
        this.modalidade = novaModalidade;
        this.valorMensal = novoValor;
        this.inicio = novoInicio;
        this.fim = novaModalidade.fimAPartirDe(novoInicio);
        this.status = novoStatus;
    }

    private static void validar(Long alunoId, Modalidade modalidade, Dinheiro valor, LocalDate inicio) {
        if (alunoId == null) {
            throw new RegraDeNegocioException("Matricula exige um aluno");
        }
        if (modalidade == null) {
            throw new RegraDeNegocioException("Matricula exige um plano (Semanal ou Mensal)");
        }
        if (valor == null || !valor.positivo()) {
            throw new RegraDeNegocioException("Valor da matricula invalido");
        }
        if (inicio == null) {
            throw new RegraDeNegocioException("Data de inicio e obrigatoria");
        }
        if (inicio.isBefore(LocalDate.now().minusYears(1)) || inicio.isAfter(LocalDate.now().plusYears(1))) {
            throw new RegraDeNegocioException("Data de inicio deve estar dentro de 1 ano a partir de hoje");
        }
    }

    public void registrarPagamento(Pagamento pagamento) {
        if (status == StatusMatricula.CANCELADA) {
            throw new RegraDeNegocioException("Nao e possivel pagar uma matricula cancelada");
        }
        if (!pagamento.valor().maiorOuIgual(valorMensal)) {
            throw new RegraDeNegocioException("Pagamento inferior ao valor do plano: " + valorMensal);
        }
        pagamentos.add(pagamento);
        if (status == StatusMatricula.VENCIDA) {
            status = StatusMatricula.ATIVA;
        }
    }

    /** Renova pelo mesmo plano contratado (mais 7 dias ou mais 1 mes). */
    public void renovar() {
        if (status == StatusMatricula.CANCELADA) {
            throw new RegraDeNegocioException("Matricula cancelada nao pode ser renovada");
        }
        LocalDate base = fim.isBefore(LocalDate.now()) ? LocalDate.now() : fim;
        this.fim = modalidade.fimAPartirDe(base);
        this.status = StatusMatricula.ATIVA;
    }

    public void cancelar() {
        cancelar("Nao informada", LocalDate.now());
    }

    public void cancelar(String justificativa, LocalDate data) {
        if (status == StatusMatricula.CANCELADA && justificativaCancelamento != null) {
            throw new RegraDeNegocioException("Matricula ja esta cancelada");
        }
        if (justificativa == null || justificativa.isBlank()) {
            throw new RegraDeNegocioException("A justificativa do cancelamento e obrigatoria");
        }
        this.status = StatusMatricula.CANCELADA;
        this.justificativaCancelamento = justificativa.trim();
        this.canceladaEm = data == null ? LocalDate.now() : data;
    }

    public boolean estaVigenteEm(LocalDate data) {
        return status == StatusMatricula.ATIVA
                && !data.isBefore(inicio)
                && !data.isAfter(fim);
    }

    /** Uma matricula "ocupa" o aluno enquanto nao estiver cancelada nem vencida. */
    public boolean ativa() {
        return status == StatusMatricula.ATIVA && !LocalDate.now().isAfter(fim);
    }

    public void atualizarVigencia(LocalDate hoje) {
        if (status == StatusMatricula.ATIVA && hoje.isAfter(fim)) {
            status = StatusMatricula.VENCIDA;
        }
    }

    public Dinheiro totalPago() {
        return pagamentos.stream().map(Pagamento::valor).reduce(Dinheiro.ZERO, Dinheiro::somar);
    }

    public void atribuirId(Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }

    public Long alunoId() {
        return alunoId;
    }

    public Modalidade modalidade() {
        return modalidade;
    }

    public String descricaoDoPlano() {
        return modalidade.nome();
    }

    public Dinheiro valorMensal() {
        return valorMensal;
    }

    public LocalDate inicio() {
        return inicio;
    }

    public LocalDate fim() {
        return fim;
    }

    public StatusMatricula status() {
        return status;
    }

    public List<Pagamento> pagamentos() {
        return Collections.unmodifiableList(pagamentos);
    }

    public String justificativaCancelamento() {
        return justificativaCancelamento;
    }

    public LocalDate canceladaEm() {
        return canceladaEm;
    }
}

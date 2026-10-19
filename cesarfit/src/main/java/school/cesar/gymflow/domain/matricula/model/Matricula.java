package school.cesar.gymflow.domain.matricula.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import school.cesar.gymflow.domain.shared.Dinheiro;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Raiz de agregado: Matricula.
 * Associa um aluno a um plano (Mensal ou Anual). O valor e SEMPRE recebido ja
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
    private final List<Pagamento> pagamentos = new ArrayList<>();
    private String justificativaDoCancelamento;
    private LocalDate dataDaSolicitacaoDeCancelamento;
    private Dinheiro taxaDeCancelamento;

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
        if (novoStatus == StatusMatricula.CANCELADA && status != StatusMatricula.CANCELADA) {
            throw new RegraDeNegocioException("Para cancelar, use o cancelamento com justificativa");
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
            throw new RegraDeNegocioException("Matricula exige um plano (Mensal ou Anual)");
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

    /** Cancelamento exige justificativa e registra a data da solicitacao. */
    public void cancelar(String justificativa, LocalDate dataDaSolicitacao) {
        if (status == StatusMatricula.CANCELADA) {
            throw new RegraDeNegocioException("Matricula ja esta cancelada");
        }
        String limpa = school.cesar.gymflow.domain.shared.Texto.obrigatorio(
                justificativa, 10, 500, "Justificativa do cancelamento");
        if (dataDaSolicitacao == null || dataDaSolicitacao.isAfter(LocalDate.now())) {
            throw new RegraDeNegocioException("Data da solicitacao de cancelamento invalida");
        }
        this.justificativaDoCancelamento = limpa;
        this.dataDaSolicitacaoDeCancelamento = dataDaSolicitacao;
        this.taxaDeCancelamento = taxaPara(dataDaSolicitacao);
        this.status = StatusMatricula.CANCELADA;
    }

    /**
     * Aviso previo: pedido feito com menos de 30 dias de antecedencia do fim do contrato
     * gera taxa de UMA mensalidade (Mensal = R$ 120,00; Anual = R$ 1320,00 / 12 = R$ 110,00).
     */
    public Dinheiro taxaPara(LocalDate dataDaSolicitacao) {
        long diasAteOFim = java.time.temporal.ChronoUnit.DAYS.between(dataDaSolicitacao, fim);
        if (diasAteOFim >= DIAS_DE_AVISO_PREVIO) {
            return Dinheiro.ZERO;
        }
        return Dinheiro.de(valorMensal.valor().divide(java.math.BigDecimal.valueOf(modalidade.meses()),
                2, java.math.RoundingMode.HALF_UP));
    }

    public static final int DIAS_DE_AVISO_PREVIO = 30;

    /** Usado pela persistencia para restaurar os dados do cancelamento. */
    public void restaurarCancelamento(String justificativa, LocalDate dataDaSolicitacao, Dinheiro taxa) {
        this.justificativaDoCancelamento = justificativa;
        this.dataDaSolicitacaoDeCancelamento = dataDaSolicitacao;
        this.taxaDeCancelamento = taxa;
    }

    public Dinheiro taxaDeCancelamento() {
        return taxaDeCancelamento;
    }

    /** Estorna o pagamento na posicao informada; o total pago deixa de contar esse valor. */
    public Pagamento estornarPagamento(int indice, String motivo) {
        if (indice < 0 || indice >= pagamentos.size()) {
            throw new RegraDeNegocioException("Pagamento nao encontrado nesta matricula");
        }
        Pagamento estornado = pagamentos.get(indice).estornar(motivo);
        pagamentos.set(indice, estornado);
        return estornado;
    }

    public Pagamento pagamento(int indice) {
        if (indice < 0 || indice >= pagamentos.size()) {
            throw new RegraDeNegocioException("Pagamento nao encontrado nesta matricula");
        }
        return pagamentos.get(indice);
    }

    public String justificativaDoCancelamento() {
        return justificativaDoCancelamento;
    }

    public LocalDate dataDaSolicitacaoDeCancelamento() {
        return dataDaSolicitacaoDeCancelamento;
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
        return pagamentos.stream().filter(p -> !p.estornado()).map(Pagamento::valor).reduce(Dinheiro.ZERO, Dinheiro::somar);
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
}

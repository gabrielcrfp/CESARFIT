package school.cesar.gymflow.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import school.cesar.gymflow.domain.matricula.model.Modalidade;
import school.cesar.gymflow.domain.matricula.model.StatusMatricula;

/** Mapeamento objeto-relacional do agregado Matricula. */
@Entity
@Table(name = "matricula")
public class MatriculaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aluno_id", nullable = false)
    private Long alunoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Modalidade modalidade;

    @Column(name = "valor_mensal", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorMensal;

    @Column(nullable = false)
    private LocalDate inicio;

    @Column(nullable = false)
    private LocalDate fim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusMatricula status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pagamento", joinColumns = @JoinColumn(name = "matricula_id"))
    @jakarta.persistence.OrderColumn(name = "posicao")
    private List<PagamentoEmbeddable> pagamentos = new ArrayList<>();

    @Column(name = "justificativa_cancelamento", length = 500)
    private String justificativaDoCancelamento;

    @Column(name = "data_solicitacao_cancelamento")
    private LocalDate dataDaSolicitacaoDeCancelamento;

    @Column(name = "taxa_cancelamento", precision = 10, scale = 2)
    private java.math.BigDecimal taxaDeCancelamento;

    protected MatriculaEntity() {
    }

    public MatriculaEntity(Long id, Long alunoId, Modalidade modalidade, BigDecimal valorMensal, LocalDate inicio, LocalDate fim,
                           StatusMatricula status, List<PagamentoEmbeddable> pagamentos) {
        this.id = id;
        this.alunoId = alunoId;
        this.modalidade = modalidade;
        this.valorMensal = valorMensal;
        this.inicio = inicio;
        this.fim = fim;
        this.status = status;
        this.pagamentos = pagamentos == null ? new ArrayList<>() : new ArrayList<>(pagamentos);
    }

    public Long getId() {
        return id;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public Modalidade getModalidade() {
        return modalidade;
    }

    public BigDecimal getValorMensal() {
        return valorMensal;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public LocalDate getFim() {
        return fim;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public List<PagamentoEmbeddable> getPagamentos() {
        return pagamentos;
    }

    public void definirCancelamento(String justificativa, LocalDate dataDaSolicitacao,
                                    java.math.BigDecimal taxa) {
        this.justificativaDoCancelamento = justificativa;
        this.dataDaSolicitacaoDeCancelamento = dataDaSolicitacao;
        this.taxaDeCancelamento = taxa;
    }

    public java.math.BigDecimal getTaxaDeCancelamento() {
        return taxaDeCancelamento;
    }

    public String getJustificativaDoCancelamento() {
        return justificativaDoCancelamento;
    }

    public LocalDate getDataDaSolicitacaoDeCancelamento() {
        return dataDaSolicitacaoDeCancelamento;
    }
}

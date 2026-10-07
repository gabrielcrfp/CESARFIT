package school.cesar.gymflow.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/** Entidade interna do agregado Matricula mapeada como colecao de elementos. */
@Embeddable
public class PagamentoEmbeddable {

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_pagamento", nullable = false)
    private LocalDate data;

    @Column(name = "forma_pagamento", nullable = false)
    private String formaDePagamento;

    @Column(name = "numero_parcela")
    private Integer numeroParcela;

    @Column(name = "vencimento")
    private LocalDate vencimento;

    protected PagamentoEmbeddable() {
    }

    public PagamentoEmbeddable(BigDecimal valor, LocalDate data, String formaDePagamento) {
        this(valor, data, formaDePagamento, null, null);
    }

    public PagamentoEmbeddable(BigDecimal valor, LocalDate data, String formaDePagamento,
                               Integer numeroParcela, LocalDate vencimento) {
        this.valor = valor;
        this.data = data;
        this.formaDePagamento = formaDePagamento;
        this.numeroParcela = numeroParcela;
        this.vencimento = vencimento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getData() {
        return data;
    }

    public String getFormaDePagamento() {
        return formaDePagamento;
    }

    public Integer getNumeroParcela() { return numeroParcela; }

    public LocalDate getVencimento() { return vencimento; }
}

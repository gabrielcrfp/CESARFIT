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

    protected PagamentoEmbeddable() {
    }

    public PagamentoEmbeddable(BigDecimal valor, LocalDate data, String formaDePagamento) {
        this.valor = valor;
        this.data = data;
        this.formaDePagamento = formaDePagamento;
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
}

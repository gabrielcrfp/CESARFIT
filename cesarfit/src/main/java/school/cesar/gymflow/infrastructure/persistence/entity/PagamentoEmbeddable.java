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

    @Column(name = "estornado")
    private Boolean estornado;

    @Column(name = "motivo_estorno", length = 300)
    private String motivoDoEstorno;

    @Column(name = "data_estorno")
    private LocalDate dataDoEstorno;

    protected PagamentoEmbeddable() {
    }

    public PagamentoEmbeddable(BigDecimal valor, LocalDate data, String formaDePagamento) {
        this.valor = valor;
        this.data = data;
        this.formaDePagamento = formaDePagamento;
    }

    public PagamentoEmbeddable(BigDecimal valor, LocalDate data, String formaDePagamento,
                               boolean estornado, String motivoDoEstorno, LocalDate dataDoEstorno) {
        this(valor, data, formaDePagamento);
        this.estornado = estornado;
        this.motivoDoEstorno = motivoDoEstorno;
        this.dataDoEstorno = dataDoEstorno;
    }

    public boolean isEstornado() {
        return Boolean.TRUE.equals(estornado);
    }

    public String getMotivoDoEstorno() {
        return motivoDoEstorno;
    }

    public LocalDate getDataDoEstorno() {
        return dataDoEstorno;
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

package school.cesar.gymflow.infrastructure.persistence.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import school.cesar.gymflow.domain.financeiro.model.StatusContestacao;

/** Mapeamento objeto-relacional (campos publicos: estrutura de dados da camada de infraestrutura). */
@Entity
@Table(name = "contestacao_cobranca")
public class ContestacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "matricula_id", nullable = false)
    public Long matriculaId;

    @Column(name = "aluno_id", nullable = false)
    public Long alunoId;

    @Column(name = "indice_pagamento", nullable = false)
    public int indiceDoPagamento;

    @Column(nullable = false, length = 30)
    public String valor;

    @Column(nullable = false, length = 500)
    public String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public StatusContestacao status;

    @Column(length = 500)
    public String parecer;

    @Column(name = "aberta_em", nullable = false)
    public LocalDateTime abertaEm;

    @Column(name = "analisada_em")
    public LocalDateTime analisadaEm;
}

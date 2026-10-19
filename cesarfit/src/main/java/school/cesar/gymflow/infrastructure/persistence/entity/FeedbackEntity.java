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

/** Mapeamento objeto-relacional (campos publicos: estrutura de dados da camada de infraestrutura). */
@Entity
@Table(name = "feedback_treino", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "data"}))
public class FeedbackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "aluno_id", nullable = false)
    public Long alunoId;

    @Column(name = "ficha_id", nullable = false)
    public Long fichaId;

    @Column(nullable = false)
    public LocalDate data;

    @Column(nullable = false)
    public int esforco;

    @Column(length = 500)
    public String observacao;

    @Column(name = "exercicios_concluidos", nullable = false)
    public int exerciciosConcluidos;

    @Column(name = "total_exercicios", nullable = false)
    public int totalDeExercicios;
}

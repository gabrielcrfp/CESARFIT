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
import school.cesar.gymflow.domain.treino.model.GrupoMuscular;

/** Mapeamento objeto-relacional (campos publicos: estrutura de dados da camada de infraestrutura). */
@Entity
@Table(name = "exercicio")
public class ExercicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 60)
    public String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "grupo_muscular", nullable = false, length = 20)
    public GrupoMuscular grupoMuscular;

    @Column(length = 60)
    public String equipamento;

    @Column(nullable = false)
    public boolean ativo;
}

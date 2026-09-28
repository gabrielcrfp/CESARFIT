package school.cesar.gymflow.infrastructure.persistence.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** Mapeamento objeto-relacional da Presenca (um check-in por aluno por dia). */
@Entity
@Table(name = "presenca", uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "data"}))
public class PresencaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aluno_id", nullable = false)
    private Long alunoId;

    @Column(nullable = false)
    private LocalDate data;

    protected PresencaEntity() {
    }

    public PresencaEntity(Long id, Long alunoId, LocalDate data) {
        this.id = id;
        this.alunoId = alunoId;
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public LocalDate getData() {
        return data;
    }
}

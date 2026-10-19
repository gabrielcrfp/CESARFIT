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
import school.cesar.gymflow.domain.atendimento.model.StatusAvaliacao;

/** Mapeamento objeto-relacional (campos publicos: estrutura de dados da camada de infraestrutura). */
@Entity
@Table(name = "solicitacao_avaliacao")
public class AvaliacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "aluno_id", nullable = false)
    public Long alunoId;

    @Column(name = "data_desejada", nullable = false)
    public LocalDateTime dataDesejada;

    @Column(name = "data_proposta")
    public LocalDateTime dataProposta;

    @Column(name = "data_agendada")
    public LocalDateTime dataAgendada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    public StatusAvaliacao status;

    @Column(length = 300)
    public String observacao;

    @Column(name = "criada_em", nullable = false)
    public LocalDateTime criadaEm;
}

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
import school.cesar.gymflow.domain.atendimento.model.StatusAtendimento;

/** Mapeamento objeto-relacional (campos publicos: estrutura de dados da camada de infraestrutura). */
@Entity
@Table(name = "solicitacao_atendimento")
public class AtendimentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "aluno_id", nullable = false)
    public Long alunoId;

    @Column(name = "ficha_id", nullable = false)
    public Long fichaId;

    @Column(length = 60)
    public String exercicio;

    @Column(nullable = false, length = 500)
    public String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public StatusAtendimento status;

    @Column(length = 80)
    public String instrutor;

    @Column(length = 500)
    public String orientacao;

    @Column(name = "aberta_em", nullable = false)
    public LocalDateTime abertaEm;

    @Column(name = "concluida_em")
    public LocalDateTime concluidaEm;
}

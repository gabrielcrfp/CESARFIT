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
@Table(name = "notificacao_vencimento", uniqueConstraints = @UniqueConstraint(columnNames = {"matricula_id", "vencimento"}))
public class NotificacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "matricula_id", nullable = false)
    public Long matriculaId;

    @Column(name = "aluno_id", nullable = false)
    public Long alunoId;

    @Column(nullable = false)
    public LocalDate vencimento;

    @Column(name = "enviada_em", nullable = false)
    public LocalDateTime enviadaEm;

    @Column(nullable = false, length = 300)
    public String mensagem;

    @Column(nullable = false)
    public boolean lida;
}

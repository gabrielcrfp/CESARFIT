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
import school.cesar.gymflow.domain.atendimento.model.StatusSolicitacao;

/** Mapeamento objeto-relacional (campos publicos: estrutura de dados da camada de infraestrutura). */
@Entity
@Table(name = "solicitacao_substituicao")
public class SubstituicaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "aluno_id", nullable = false)
    public Long alunoId;

    @Column(name = "ficha_id", nullable = false)
    public Long fichaId;

    @Column(nullable = false)
    public int posicao;

    @Column(name = "exercicio_atual", nullable = false, length = 60)
    public String exercicioAtual;

    @Column(nullable = false, length = 500)
    public String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public StatusSolicitacao status;

    @Column(name = "exercicio_novo", length = 60)
    public String exercicioNovo;

    @Column(length = 300)
    public String resposta;

    @Column(name = "criada_em", nullable = false)
    public LocalDateTime criadaEm;

    @Column(name = "respondida_em")
    public LocalDateTime respondidaEm;
}

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
import school.cesar.gymflow.domain.operacao.model.StatusOcorrencia;

/** Mapeamento objeto-relacional (campos publicos: estrutura de dados da camada de infraestrutura). */
@Entity
@Table(name = "ocorrencia_equipamento")
public class OcorrenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 80)
    public String equipamento;

    @Column(nullable = false, length = 500)
    public String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    public StatusOcorrencia status;

    @Column(name = "aberta_em", nullable = false)
    public LocalDateTime abertaEm;

    @Column(name = "atualizada_em", nullable = false)
    public LocalDateTime atualizadaEm;

    @Column(length = 500)
    public String solucao;
}

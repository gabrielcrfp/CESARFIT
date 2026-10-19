package school.cesar.gymflow.infrastructure.persistence.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import school.cesar.gymflow.domain.matricula.model.Objetivo;

/** Mapeamento objeto-relacional do agregado FichaDeTreino. */
@Entity
@Table(name = "ficha_treino")
public class FichaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "aluno_id", nullable = false)
    private Long alunoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Objetivo objetivo;

    @Column(name = "criada_em", nullable = false)
    private LocalDate criadaEm;

    @Column(name = "valida_ate", nullable = false)
    private LocalDate validaAte;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "item_treino", joinColumns = @JoinColumn(name = "ficha_id"))
    @OrderColumn(name = "posicao")
    private List<ItemEmbeddable> itens = new ArrayList<>();

    protected FichaEntity() {
    }

    public FichaEntity(Long id, Long alunoId, Objetivo objetivo, LocalDate criadaEm,
                       LocalDate validaAte, List<ItemEmbeddable> itens) {
        this.id = id;
        this.alunoId = alunoId;
        this.objetivo = objetivo;
        this.criadaEm = criadaEm;
        this.validaAte = validaAte;
        this.itens = itens == null ? new ArrayList<>() : new ArrayList<>(itens);
    }

    public Long getId() {
        return id;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public Objetivo getObjetivo() {
        return objetivo;
    }

    public LocalDate getCriadaEm() {
        return criadaEm;
    }

    public LocalDate getValidaAte() {
        return validaAte;
    }

    public List<ItemEmbeddable> getItens() {
        return itens;
    }
}

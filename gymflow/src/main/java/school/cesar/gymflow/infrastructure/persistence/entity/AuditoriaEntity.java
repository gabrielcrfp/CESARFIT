package school.cesar.gymflow.infrastructure.persistence.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Registro append-only da trilha de auditoria do dominio. */
@Entity
@Table(name = "auditoria")
public class AuditoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String tipo;

    @Column(nullable = false, length = 120)
    private String referencia;

    @Column(nullable = false, length = 1000)
    private String detalhe;

    @Column(nullable = false)
    private LocalDateTime ocorridoEm;

    protected AuditoriaEntity() { }

    public AuditoriaEntity(String tipo, String referencia, String detalhe, LocalDateTime ocorridoEm) {
        this.tipo = tipo;
        this.referencia = referencia;
        this.detalhe = detalhe;
        this.ocorridoEm = ocorridoEm;
    }

    public Long getId() { return id; }
    public String getTipo() { return tipo; }
    public String getReferencia() { return referencia; }
    public String getDetalhe() { return detalhe; }
    public LocalDateTime getOcorridoEm() { return ocorridoEm; }
}

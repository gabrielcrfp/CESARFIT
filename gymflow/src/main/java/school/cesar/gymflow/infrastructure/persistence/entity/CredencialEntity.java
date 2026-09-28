package school.cesar.gymflow.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import school.cesar.gymflow.domain.acesso.model.Perfil;

/** Mapeamento objeto-relacional do agregado Credencial. */
@Entity
@Table(name = "credencial")
public class CredencialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 30)
    private String login;

    @Column(name = "aluno_id", unique = true)
    private Long alunoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Perfil perfil;

    private String nome;

    @Column(name = "senha", nullable = false)
    private String senhaCodificada;

    protected CredencialEntity() {
    }

    public CredencialEntity(Long id, String login, Long alunoId, Perfil perfil, String nome, String senhaCodificada) {
        this.id = id;
        this.login = login;
        this.alunoId = alunoId;
        this.perfil = perfil;
        this.nome = nome;
        this.senhaCodificada = senhaCodificada;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public String getNome() {
        return nome;
    }

    public String getSenhaCodificada() {
        return senhaCodificada;
    }
}

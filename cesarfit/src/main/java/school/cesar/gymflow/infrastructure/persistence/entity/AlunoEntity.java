package school.cesar.gymflow.infrastructure.persistence.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import school.cesar.gymflow.domain.matricula.model.Objetivo;

/** Mapeamento objeto-relacional do agregado Aluno. */
@Entity
@Table(name = "aluno")
public class AlunoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(length = 11)
    private String telefone;

    @Column(nullable = false)
    private String email;

    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataDeNascimento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Objetivo objetivo;

    protected AlunoEntity() {
    }

    public AlunoEntity(Long id, String nome, String cpf, String telefone, String email,
                       LocalDate dataDeNascimento, Objetivo objetivo) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.dataDeNascimento = dataDeNascimento;
        this.objetivo = objetivo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDataDeNascimento() {
        return dataDeNascimento;
    }

    public Objetivo getObjetivo() {
        return objetivo;
    }
}

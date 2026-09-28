package school.cesar.gymflow.domain.matricula.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

import school.cesar.gymflow.domain.shared.Cpf;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Raiz de agregado: Aluno. Todas as regras de validacao do cadastro ficam aqui. */
public class Aluno {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final int IDADE_MINIMA = 16;

    private Long id;
    private String nome;
    private Cpf cpf;
    private String telefone;
    private String email;
    private LocalDate dataDeNascimento;
    private Objetivo objetivo;

    public Aluno(Long id, String nome, Cpf cpf, String telefone, String email,
                 LocalDate dataDeNascimento, Objetivo objetivo) {
        this.id = id;
        aplicar(nome, cpf, telefone, email, dataDeNascimento, objetivo);
    }

    /** Edicao completa dos dados do aluno (mesmas validacoes do cadastro). */
    public void atualizar(String nome, Cpf cpf, String telefone, String email,
                          LocalDate dataDeNascimento, Objetivo objetivo) {
        aplicar(nome, cpf, telefone, email, dataDeNascimento, objetivo);
    }

    private void aplicar(String nome, Cpf cpf, String telefone, String email,
                         LocalDate dataDeNascimento, Objetivo objetivo) {
        if (nome == null || nome.trim().length() < 3) {
            throw new RegraDeNegocioException("Nome do aluno e obrigatorio (minimo 3 letras)");
        }
        if (cpf == null) {
            throw new RegraDeNegocioException("CPF e obrigatorio");
        }
        String digitosTelefone = telefone == null ? "" : telefone.replaceAll("\\D", "");
        if (digitosTelefone.length() < 10 || digitosTelefone.length() > 11) {
            throw new RegraDeNegocioException("Telefone invalido: informe DDD + numero (10 ou 11 digitos)");
        }
        if (email == null || !EMAIL.matcher(email.trim()).matches()) {
            throw new RegraDeNegocioException("E-mail invalido: " + (email == null ? "" : email));
        }
        if (dataDeNascimento == null || dataDeNascimento.isAfter(LocalDate.now())) {
            throw new RegraDeNegocioException("Data de nascimento invalida");
        }
        if (Period.between(dataDeNascimento, LocalDate.now()).getYears() < IDADE_MINIMA) {
            throw new RegraDeNegocioException("Idade minima para cadastro e " + IDADE_MINIMA + " anos");
        }
        this.nome = nome.trim();
        this.cpf = cpf;
        this.telefone = digitosTelefone;
        this.email = email.trim().toLowerCase();
        this.dataDeNascimento = dataDeNascimento;
        this.objetivo = objetivo == null ? Objetivo.CONDICIONAMENTO : objetivo;
    }

    public int idade() {
        return Period.between(dataDeNascimento, LocalDate.now()).getYears();
    }

    public String telefoneFormatado() {
        if (telefone.length() == 11) {
            return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 7) + "-" + telefone.substring(7);
        }
        return "(" + telefone.substring(0, 2) + ") " + telefone.substring(2, 6) + "-" + telefone.substring(6);
    }

    public void redefinirObjetivo(Objetivo novoObjetivo) {
        if (novoObjetivo == null) {
            throw new RegraDeNegocioException("Objetivo e obrigatorio");
        }
        this.objetivo = novoObjetivo;
    }

    public void atribuirId(Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }

    public String nome() {
        return nome;
    }

    public Cpf cpf() {
        return cpf;
    }

    public String telefone() {
        return telefone;
    }

    public String email() {
        return email;
    }

    public LocalDate dataDeNascimento() {
        return dataDeNascimento;
    }

    public Objetivo objetivo() {
        return objetivo;
    }
}

package school.cesar.gymflow.domain.acesso.model;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Raiz de agregado: Credencial.
 * Guarda a senha (sempre codificada) de um aluno ou de um membro da equipe.
 * O aluno entra com o numero da matricula; a equipe entra com o seu numero de acesso.
 */
public class Credencial {

    public static final int TAMANHO_MINIMO_DA_SENHA = 6;

    private Long id;
    private final String login;
    private final Long alunoId;
    private final Perfil perfil;
    private final String nome;
    private String senhaCodificada;

    public Credencial(Long id, String login, Long alunoId, Perfil perfil, String nome, String senhaCodificada) {
        if (perfil == null) {
            throw new RegraDeNegocioException("Perfil de acesso e obrigatorio");
        }
        if (perfil == Perfil.ALUNO && alunoId == null) {
            throw new RegraDeNegocioException("Credencial de aluno exige o aluno");
        }
        if (perfil == Perfil.EQUIPE && (login == null || login.isBlank())) {
            throw new RegraDeNegocioException("Credencial da equipe exige o numero de acesso");
        }
        if (senhaCodificada == null || senhaCodificada.isBlank()) {
            throw new RegraDeNegocioException("Senha e obrigatoria");
        }
        this.id = id;
        this.login = login == null ? null : login.trim().toUpperCase();
        this.alunoId = alunoId;
        this.perfil = perfil;
        this.nome = nome;
        this.senhaCodificada = senhaCodificada;
    }

    public static Credencial doAluno(Long alunoId, String senhaCodificada) {
        return new Credencial(null, null, alunoId, Perfil.ALUNO, null, senhaCodificada);
    }

    public static Credencial daEquipe(String login, String nome, String senhaCodificada) {
        return new Credencial(null, login, null, Perfil.EQUIPE, nome, senhaCodificada);
    }

    /** Regra da senha em texto puro, verificada antes de codificar. */
    public static void validarNovaSenha(String senha) {
        if (senha == null || senha.length() < TAMANHO_MINIMO_DA_SENHA) {
            throw new RegraDeNegocioException("A nova senha deve ter pelo menos "
                    + TAMANHO_MINIMO_DA_SENHA + " caracteres");
        }
    }

    public void trocarSenha(String novaSenhaCodificada) {
        if (novaSenhaCodificada == null || novaSenhaCodificada.isBlank()) {
            throw new RegraDeNegocioException("Senha e obrigatoria");
        }
        this.senhaCodificada = novaSenhaCodificada;
    }

    public void atribuirId(Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }

    public String login() {
        return login;
    }

    public Long alunoId() {
        return alunoId;
    }

    public Perfil perfil() {
        return perfil;
    }

    public String nome() {
        return nome;
    }

    public String senhaCodificada() {
        return senhaCodificada;
    }
}

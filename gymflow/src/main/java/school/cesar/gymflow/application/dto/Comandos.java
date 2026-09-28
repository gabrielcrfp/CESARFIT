package school.cesar.gymflow.application.dto;

import java.time.LocalDate;

import school.cesar.gymflow.domain.matricula.model.Objetivo;

/** Comandos de entrada dos casos de uso (camada de aplicacao). */
public final class Comandos {

    private Comandos() {
    }

    /** Usado tanto no cadastro quanto na edicao do aluno. */
    public record SalvarAluno(String nome, String cpf, String telefone, String email,
                              LocalDate dataDeNascimento, Objetivo objetivo) {
    }

    /**
     * Usado no cadastro e na edicao da matricula. O valor NAO vem da interface:
     * e sempre calculado no servidor a partir do plano escolhido.
     */
    public record SalvarMatricula(Long alunoId, String plano, LocalDate inicio, String status) {
    }

    public record RegistrarPagamento(Long matriculaId, String valor, String formaDePagamento) {
    }

    public record ConcluirItem(Long fichaId, int posicao) {
    }

    public record Entrar(String numero, String senha) {
    }

    public record AlterarSenha(String senhaAtual, String novaSenha) {
    }
}

package school.cesar.gymflow.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.domain.acesso.model.Credencial;
import school.cesar.gymflow.domain.acesso.repositorio.RepositorioDeCredenciais;
import school.cesar.gymflow.domain.acesso.servico.CodificadorDeSenha;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Caso de uso: o aluno troca a propria senha de acesso. */
@Service
public class AlterarSenhaUseCase {

    private final RepositorioDeCredenciais credenciais;
    private final RepositorioDeAlunos alunos;
    private final CodificadorDeSenha codificador;
    private final PublicadorDeEventos publicador;

    public AlterarSenhaUseCase(RepositorioDeCredenciais credenciais, RepositorioDeAlunos alunos,
                               CodificadorDeSenha codificador, PublicadorDeEventos publicador) {
        this.credenciais = credenciais;
        this.alunos = alunos;
        this.codificador = codificador;
        this.publicador = publicador;
    }

    @Transactional
    public void executar(Long alunoId, Comandos.AlterarSenha comando) {
        Aluno aluno = alunos.porId(alunoId)
                .orElseThrow(() -> new RegraDeNegocioException("Aluno nao encontrado"));
        String atual = comando.senhaAtual() == null ? "" : comando.senhaAtual();
        Credencial existente = credenciais.doAluno(alunoId).orElse(null);
        boolean confere = existente == null
                ? atual.equals(EntrarNoSistemaUseCase.senhaDePrimeiroAcesso(aluno))
                : codificador.confere(atual, existente.senhaCodificada());
        if (!confere) {
            throw new RegraDeNegocioException("Senha atual incorreta");
        }
        Credencial.validarNovaSenha(comando.novaSenha());
        if (comando.novaSenha().equals(atual)) {
            throw new RegraDeNegocioException("A nova senha deve ser diferente da atual");
        }
        String codificada = codificador.codificar(comando.novaSenha());
        if (existente == null) {
            credenciais.salvar(Credencial.doAluno(alunoId, codificada));
        } else {
            existente.trocarSenha(codificada);
            credenciais.salvar(existente);
        }
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.SENHA_ALTERADA,
                "aluno " + alunoId, aluno.nome() + " alterou a senha de acesso"));
    }
}

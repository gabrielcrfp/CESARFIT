package school.cesar.gymflow.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.acesso.model.Credencial;
import school.cesar.gymflow.domain.acesso.model.Perfil;
import school.cesar.gymflow.domain.acesso.repositorio.RepositorioDeCredenciais;
import school.cesar.gymflow.domain.acesso.servico.CodificadorDeSenha;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.model.StatusMatricula;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Caso de uso: entrar no sistema com numero da matricula e senha.
 * Numero so com digitos = matricula de aluno; numero com letras (ex.: EQ001) = equipe.
 * Enquanto o aluno nao troca a senha, a senha de primeiro acesso sao os 6 primeiros digitos do CPF.
 */
@Service
public class EntrarNoSistemaUseCase {

    static final String CREDENCIAIS_INVALIDAS = "Numero da matricula ou senha invalidos";

    private final RepositorioDeCredenciais credenciais;
    private final RepositorioDeMatriculas matriculas;
    private final RepositorioDeAlunos alunos;
    private final CodificadorDeSenha codificador;
    private final GerenciadorDeSessoes sessoes;

    public EntrarNoSistemaUseCase(RepositorioDeCredenciais credenciais, RepositorioDeMatriculas matriculas,
                                  RepositorioDeAlunos alunos, CodificadorDeSenha codificador,
                                  GerenciadorDeSessoes sessoes) {
        this.credenciais = credenciais;
        this.matriculas = matriculas;
        this.alunos = alunos;
        this.codificador = codificador;
        this.sessoes = sessoes;
    }

    @Transactional(readOnly = true)
    public Respostas.Sessao executar(Comandos.Entrar comando) {
        String numero = comando.numero() == null ? "" : comando.numero().trim();
        String senha = comando.senha() == null ? "" : comando.senha();
        if (numero.isEmpty() || senha.isEmpty()) {
            throw new RegraDeNegocioException("Informe o numero da matricula e a senha");
        }
        GerenciadorDeSessoes.SessaoAtiva sessao = numero.matches("\\d+")
                ? entrarComoAluno(Long.parseLong(numero), senha)
                : entrarComoEquipe(numero, senha);
        return new Respostas.Sessao(sessao.token(), sessao.perfil().name(), sessao.nome(), sessao.alunoId());
    }

    private GerenciadorDeSessoes.SessaoAtiva entrarComoAluno(Long numeroDaMatricula, String senha) {
        Matricula matricula = matriculas.porId(numeroDaMatricula)
                .orElseThrow(() -> new RegraDeNegocioException(CREDENCIAIS_INVALIDAS));
        Aluno aluno = alunos.porId(matricula.alunoId())
                .orElseThrow(() -> new RegraDeNegocioException(CREDENCIAIS_INVALIDAS));
        boolean senhaConfere = credenciais.doAluno(aluno.id())
                .map(c -> codificador.confere(senha, c.senhaCodificada()))
                .orElseGet(() -> senha.equals(senhaDePrimeiroAcesso(aluno)));
        if (!senhaConfere) {
            throw new RegraDeNegocioException(CREDENCIAIS_INVALIDAS);
        }
        if (matricula.status() == StatusMatricula.CANCELADA) {
            throw new RegraDeNegocioException("Matricula cancelada: procure a recepcao");
        }
        return sessoes.abrir(Perfil.ALUNO, aluno.id(), aluno.nome());
    }

    private GerenciadorDeSessoes.SessaoAtiva entrarComoEquipe(String numero, String senha) {
        Credencial credencial = credenciais.daEquipe(numero)
                .filter(c -> codificador.confere(senha, c.senhaCodificada()))
                .orElseThrow(() -> new RegraDeNegocioException(CREDENCIAIS_INVALIDAS));
        return sessoes.abrir(Perfil.EQUIPE, null, credencial.nome());
    }

    static String senhaDePrimeiroAcesso(Aluno aluno) {
        return aluno.cpf().numero().substring(0, 6);
    }
}

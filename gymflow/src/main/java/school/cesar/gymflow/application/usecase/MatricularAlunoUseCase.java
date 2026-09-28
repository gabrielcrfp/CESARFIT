package school.cesar.gymflow.application.usecase;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.model.Modalidade;
import school.cesar.gymflow.domain.matricula.politica.SeletorDePolitica;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.Dinheiro;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Caso de uso: cadastrar matricula associando aluno a um plano (media - Integrante 3).
 * O valor e calculado pela politica de preco (Strategy) do plano escolhido.
 */
@Service
public class MatricularAlunoUseCase {

    private final RepositorioDeAlunos repositorioDeAlunos;
    private final RepositorioDeMatriculas repositorioDeMatriculas;
    private final SeletorDePolitica seletorDePolitica;
    private final PublicadorDeEventos publicador;

    public MatricularAlunoUseCase(RepositorioDeAlunos repositorioDeAlunos,
                                  RepositorioDeMatriculas repositorioDeMatriculas,
                                  SeletorDePolitica seletorDePolitica,
                                  PublicadorDeEventos publicador) {
        this.repositorioDeAlunos = repositorioDeAlunos;
        this.repositorioDeMatriculas = repositorioDeMatriculas;
        this.seletorDePolitica = seletorDePolitica;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.MatriculaResumo executar(Comandos.SalvarMatricula comando) {
        if (comando == null || comando.alunoId() == null) {
            throw new RegraDeNegocioException("Selecione o aluno da matricula");
        }
        Aluno aluno = repositorioDeAlunos.porId(comando.alunoId())
                .orElseThrow(() -> new RegraDeNegocioException("Aluno nao encontrado"));
        Modalidade plano = Modalidade.de(comando.plano());

        boolean jaPossuiAtiva = repositorioDeMatriculas.porAluno(aluno.id()).stream()
                .anyMatch(Matricula::ativa);
        if (jaPossuiAtiva) {
            throw new RegraDeNegocioException(aluno.nome()
                    + " ja possui matricula ativa. Edite a matricula existente para trocar de plano.");
        }

        Dinheiro valor = seletorDePolitica.valorDe(plano);
        LocalDate inicio = comando.inicio() == null ? LocalDate.now() : comando.inicio();
        Matricula salva = repositorioDeMatriculas.salvar(Matricula.abrir(aluno.id(), plano, valor, inicio));

        publicador.publicar(EventoDeDominio.de(EventoDeDominio.MATRICULA_EFETIVADA,
                String.valueOf(salva.id()),
                "Aluno " + aluno.nome() + " matriculado no " + plano.nome() + " por " + valor));

        return Resumos.matricula(salva, aluno.nome());
    }
}

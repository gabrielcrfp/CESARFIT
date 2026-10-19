package school.cesar.gymflow.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.model.Modalidade;
import school.cesar.gymflow.domain.matricula.model.StatusMatricula;
import school.cesar.gymflow.domain.matricula.politica.SeletorDePolitica;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.Dinheiro;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Caso de uso: editar matricula (media - Integrante 4).
 * Permite trocar aluno, plano, data de inicio e situacao. Sempre recalcula o valor
 * pelo plano escolhido (Mensal = R$ 120,00 / Anual = R$ 1320,00) e SALVA o novo valor.
 */
@Service
public class AtualizarMatriculaUseCase {

    private final RepositorioDeAlunos repositorioDeAlunos;
    private final RepositorioDeMatriculas repositorioDeMatriculas;
    private final SeletorDePolitica seletorDePolitica;
    private final PublicadorDeEventos publicador;

    public AtualizarMatriculaUseCase(RepositorioDeAlunos repositorioDeAlunos,
                                     RepositorioDeMatriculas repositorioDeMatriculas,
                                     SeletorDePolitica seletorDePolitica,
                                     PublicadorDeEventos publicador) {
        this.repositorioDeAlunos = repositorioDeAlunos;
        this.repositorioDeMatriculas = repositorioDeMatriculas;
        this.seletorDePolitica = seletorDePolitica;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.MatriculaResumo executar(Long matriculaId, Comandos.SalvarMatricula comando) {
        if (comando == null || comando.alunoId() == null) {
            throw new RegraDeNegocioException("Selecione o aluno da matricula");
        }
        Matricula matricula = repositorioDeMatriculas.porId(matriculaId)
                .orElseThrow(() -> new RegraDeNegocioException("Matricula nao encontrada"));
        Aluno aluno = repositorioDeAlunos.porId(comando.alunoId())
                .orElseThrow(() -> new RegraDeNegocioException("Aluno nao encontrado"));
        Modalidade novoPlano = Modalidade.de(comando.plano());
        StatusMatricula novoStatus = StatusMatricula.de(comando.status());
        if (comando.inicio() == null) {
            throw new RegraDeNegocioException("Data de inicio e obrigatoria");
        }

        if (novoStatus == StatusMatricula.ATIVA) {
            boolean conflito = repositorioDeMatriculas.porAluno(aluno.id()).stream()
                    .anyMatch(outra -> !outra.id().equals(matriculaId) && outra.ativa());
            if (conflito) {
                throw new RegraDeNegocioException(aluno.nome() + " ja possui outra matricula ativa");
            }
        }

        String planoAnterior = matricula.descricaoDoPlano();
        Dinheiro valorAnterior = matricula.valorMensal();
        Dinheiro novoValor = seletorDePolitica.valorDe(novoPlano);

        matricula.alterar(aluno.id(), novoPlano, novoValor, comando.inicio(), novoStatus);
        Matricula salva = repositorioDeMatriculas.salvar(matricula);

        publicador.publicar(EventoDeDominio.de(EventoDeDominio.MATRICULA_ALTERADA,
                String.valueOf(salva.id()), planoAnterior + " (" + valorAnterior + ") -> "
                        + salva.descricaoDoPlano() + " (" + salva.valorMensal() + "), situacao "
                        + salva.status()));

        return Resumos.matricula(salva, aluno.nome());
    }
}

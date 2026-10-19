package school.cesar.gymflow.application.usecase;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.treino.model.Presenca;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDePresencas;

/** Caso de uso: check-in do dia (uma presenca por dia, exige matricula vigente). */
@Service
public class RegistrarPresencaUseCase {

    private final RepositorioDePresencas presencas;
    private final RepositorioDeAlunos alunos;
    private final RepositorioDeMatriculas matriculas;
    private final PublicadorDeEventos publicador;

    public RegistrarPresencaUseCase(RepositorioDePresencas presencas, RepositorioDeAlunos alunos,
                                    RepositorioDeMatriculas matriculas, PublicadorDeEventos publicador) {
        this.presencas = presencas;
        this.alunos = alunos;
        this.matriculas = matriculas;
        this.publicador = publicador;
    }

    @Transactional
    public void executar(Long alunoId) {
        registrarEm(alunoId, LocalDate.now());
    }

    /** Tambem usado pela carga inicial para montar o historico de demonstracao. */
    @Transactional
    public void registrarEm(Long alunoId, LocalDate data) {
        Aluno aluno = alunos.porId(alunoId)
                .orElseThrow(() -> new RegraDeNegocioException("Aluno nao encontrado"));
        if (matriculas.vigentePorAluno(alunoId).isEmpty()) {
            throw new RegraDeNegocioException("Check-in exige matricula ativa");
        }
        if (presencas.existe(alunoId, data)) {
            throw new RegraDeNegocioException("Check-in de hoje ja foi registrado");
        }
        presencas.salvar(new Presenca(null, alunoId, data));
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.PRESENCA_REGISTRADA,
                "aluno " + alunoId, aluno.nome() + " fez check-in em " + data));
    }
}

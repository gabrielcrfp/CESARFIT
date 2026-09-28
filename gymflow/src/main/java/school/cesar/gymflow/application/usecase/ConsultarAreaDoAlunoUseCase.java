package school.cesar.gymflow.application.usecase;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.treino.model.Frequencia;
import school.cesar.gymflow.domain.treino.model.Presenca;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFichas;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDePresencas;

/** Monta a area do aluno: dados pessoais, matricula, ficha da semana e frequencia. */
@Service
public class ConsultarAreaDoAlunoUseCase {

    private static final String[] ROTULOS = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};

    private final RepositorioDeAlunos alunos;
    private final RepositorioDeMatriculas matriculas;
    private final RepositorioDeFichas fichas;
    private final RepositorioDePresencas presencas;

    public ConsultarAreaDoAlunoUseCase(RepositorioDeAlunos alunos, RepositorioDeMatriculas matriculas,
                                       RepositorioDeFichas fichas, RepositorioDePresencas presencas) {
        this.alunos = alunos;
        this.matriculas = matriculas;
        this.fichas = fichas;
        this.presencas = presencas;
    }

    @Transactional(readOnly = true)
    public Respostas.AreaDoAluno executar(Long alunoId) {
        Aluno aluno = alunos.porId(alunoId)
                .orElseThrow(() -> new RegraDeNegocioException("Aluno nao encontrado"));
        List<Matricula> doAluno = matriculas.porAluno(alunoId);
        Respostas.MatriculaResumo matricula = matriculas.vigentePorAluno(alunoId)
                .or(() -> doAluno.stream().findFirst())
                .map(m -> Resumos.matricula(m, aluno.nome()))
                .orElse(null);
        Respostas.FichaResumo ficha = fichas.porAluno(alunoId).stream().findFirst()
                .map(GerarFichaDeTreinoUseCase::montarResumo)
                .orElse(null);
        return new Respostas.AreaDoAluno(Resumos.aluno(aluno, doAluno), matricula, ficha,
                frequencia(alunoId, LocalDate.now()));
    }

    /** Garante que o aluno so mexe na propria ficha. */
    @Transactional(readOnly = true)
    public boolean fichaPertenceAoAluno(Long fichaId, Long alunoId) {
        return fichas.porId(fichaId).map(f -> alunoId.equals(f.alunoId())).orElse(false);
    }

    private Respostas.FrequenciaResumo frequencia(Long alunoId, LocalDate hoje) {
        Frequencia frequencia = new Frequencia(presencas.porAluno(alunoId).stream().map(Presenca::data).toList());
        LocalDate segunda = hoje.with(DayOfWeek.MONDAY);
        List<Respostas.DiaDeFrequencia> semana = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate dia = segunda.plusDays(i);
            semana.add(new Respostas.DiaDeFrequencia(dia, ROTULOS[i], frequencia.presenteEm(dia), dia.equals(hoje)));
        }
        return new Respostas.FrequenciaResumo(frequencia.sequenciaAtual(hoje), frequencia.maiorSequencia(),
                frequencia.presencasNoMes(YearMonth.from(hoje)), frequencia.total(),
                frequencia.presenteEm(hoje), semana);
    }
}

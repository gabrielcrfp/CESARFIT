package school.cesar.gymflow.application.usecase;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.treino.model.FichaDeTreino;
import school.cesar.gymflow.domain.treino.model.ItemDeTreino;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeExercicios;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFichas;
import school.cesar.gymflow.domain.treino.servico.FabricaDeGerador;
import school.cesar.gymflow.domain.treino.servico.GeradorDeFicha;

/**
 * Caso de uso: gerar ficha de treino (complexidade alta).
 * Usa Template Method para montar a ficha conforme o objetivo do aluno.
 */
@Service
public class GerarFichaDeTreinoUseCase {

    private final RepositorioDeAlunos repositorioDeAlunos;
    private final RepositorioDeMatriculas repositorioDeMatriculas;
    private final RepositorioDeFichas repositorioDeFichas;
    private final PublicadorDeEventos publicador;
    private final RepositorioDeExercicios repositorioDeExercicios;

    public GerarFichaDeTreinoUseCase(RepositorioDeAlunos repositorioDeAlunos,
                                     RepositorioDeMatriculas repositorioDeMatriculas,
                                     RepositorioDeFichas repositorioDeFichas,
                                     PublicadorDeEventos publicador,
                                     RepositorioDeExercicios repositorioDeExercicios) {
        this.repositorioDeAlunos = repositorioDeAlunos;
        this.repositorioDeMatriculas = repositorioDeMatriculas;
        this.repositorioDeFichas = repositorioDeFichas;
        this.publicador = publicador;
        this.repositorioDeExercicios = repositorioDeExercicios;
    }

    @Transactional
    public Respostas.FichaResumo executar(Long alunoId) {
        Aluno aluno = repositorioDeAlunos.porId(alunoId)
                .orElseThrow(() -> new RegraDeNegocioException("Aluno nao encontrado"));
        repositorioDeMatriculas.vigentePorAluno(alunoId)
                .orElseThrow(() -> new RegraDeNegocioException(
                        "Aluno sem matricula vigente nao pode receber ficha"));

        GeradorDeFicha gerador = FabricaDeGerador.para(aluno.objetivo());
        FichaDeTreino ficha = gerador.gerar(aluno, repositorioDeExercicios.ativos());
        FichaDeTreino salva = repositorioDeFichas.salvar(ficha);

        publicador.publicar(EventoDeDominio.de(EventoDeDominio.FICHA_GERADA,
                String.valueOf(salva.id()),
                "Ficha de " + aluno.objetivo() + " gerada para " + aluno.nome()));

        return montarResumo(salva);
    }

    static Respostas.FichaResumo montarResumo(FichaDeTreino ficha) {
        List<Respostas.ItemResumo> itens = new ArrayList<>();
        List<ItemDeTreino> originais = ficha.itens();
        for (int i = 0; i < originais.size(); i++) {
            ItemDeTreino item = originais.get(i);
            itens.add(new Respostas.ItemResumo(i, item.exercicio(), item.grupoMuscular().name(),
                    item.series(), item.repeticoes(), item.descansoEmSegundos(), item.concluido(),
                    item.divisao()));
        }
        return new Respostas.FichaResumo(ficha.id(), ficha.alunoId(), ficha.objetivo().name(),
                ficha.criadaEm(), ficha.validaAte(), ficha.totalPendentes(), itens);
    }
}

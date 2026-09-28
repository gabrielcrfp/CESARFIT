package school.cesar.gymflow.domain.treino.servico;

import java.time.LocalDate;
import java.util.List;

import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.treino.model.FichaDeTreino;
import school.cesar.gymflow.domain.treino.model.ItemDeTreino;

/**
 * Padrao Template Method.
 * O esqueleto da geracao da ficha e fixo; as subclasses definem os exercicios,
 * o volume e a duracao conforme o objetivo do aluno.
 */
public abstract class GeradorDeFicha {

    /** Template method - nao pode ser sobrescrito. */
    public final FichaDeTreino gerar(Aluno aluno) {
        validar(aluno);
        List<ItemDeTreino> itens = montarItens();
        if (itens.isEmpty()) {
            throw new RegraDeNegocioException("Ficha gerada sem exercicios");
        }
        LocalDate hoje = LocalDate.now();
        FichaDeTreino ficha = new FichaDeTreino(null, aluno.id(), aluno.objetivo(),
                hoje, hoje.plusWeeks(semanasDeValidade()), itens);
        aoFinalizar(ficha);
        return ficha;
    }

    /** Passo comum (hook com implementacao padrao). */
    protected void validar(Aluno aluno) {
        if (aluno == null || aluno.id() == null) {
            throw new RegraDeNegocioException("Aluno invalido para geracao de ficha");
        }
    }

    /** Passo obrigatorio definido pelas subclasses. */
    protected abstract List<ItemDeTreino> montarItens();

    /** Passo obrigatorio definido pelas subclasses. */
    protected abstract int semanasDeValidade();

    /** Hook opcional. */
    protected void aoFinalizar(FichaDeTreino ficha) {
        // por padrao, nada
    }
}

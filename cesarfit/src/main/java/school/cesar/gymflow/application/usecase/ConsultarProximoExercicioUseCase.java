package school.cesar.gymflow.application.usecase;

import java.util.Iterator;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.treino.model.FichaDeTreino;
import school.cesar.gymflow.domain.treino.model.ItemDeTreino;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFichas;

/**
 * Caso de uso: consultar o proximo exercicio pendente (complexidade baixa).
 * Consome o Iterator publicado pelo agregado FichaDeTreino.
 */
@Service
public class ConsultarProximoExercicioUseCase {

    private final RepositorioDeFichas repositorioDeFichas;

    public ConsultarProximoExercicioUseCase(RepositorioDeFichas repositorioDeFichas) {
        this.repositorioDeFichas = repositorioDeFichas;
    }

    @Transactional(readOnly = true)
    public Optional<Respostas.ItemResumo> executar(Long fichaId) {
        FichaDeTreino ficha = repositorioDeFichas.porId(fichaId)
                .orElseThrow(() -> new RegraDeNegocioException("Ficha nao encontrada"));

        Iterator<ItemDeTreino> iterador = ficha.iterator();
        if (!iterador.hasNext()) {
            return Optional.empty();
        }
        ItemDeTreino proximo = iterador.next();
        int posicao = ficha.itens().indexOf(proximo);
        return Optional.of(new Respostas.ItemResumo(posicao, proximo.exercicio(),
                proximo.grupoMuscular().name(), proximo.series(), proximo.repeticoes(),
                proximo.descansoEmSegundos(), proximo.concluido(), proximo.divisao()));
    }
}

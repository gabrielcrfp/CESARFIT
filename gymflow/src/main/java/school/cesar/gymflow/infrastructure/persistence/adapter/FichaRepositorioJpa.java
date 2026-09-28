package school.cesar.gymflow.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import school.cesar.gymflow.domain.treino.model.FichaDeTreino;
import school.cesar.gymflow.domain.treino.model.ItemDeTreino;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFichas;
import school.cesar.gymflow.infrastructure.persistence.entity.FichaEntity;
import school.cesar.gymflow.infrastructure.persistence.entity.ItemEmbeddable;
import school.cesar.gymflow.infrastructure.persistence.springdata.FichaJpaRepository;

/** Adaptador de saida do agregado FichaDeTreino. */
@Repository
public class FichaRepositorioJpa implements RepositorioDeFichas {

    private final FichaJpaRepository jpa;

    public FichaRepositorioJpa(FichaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public FichaDeTreino salvar(FichaDeTreino ficha) {
        List<ItemEmbeddable> itens = ficha.itens().stream()
                .map(i -> new ItemEmbeddable(i.exercicio(), i.grupoMuscular(), i.series(),
                        i.repeticoes(), i.descansoEmSegundos(), i.concluido()))
                .toList();
        FichaEntity entidade = new FichaEntity(ficha.id(), ficha.alunoId(), ficha.objetivo(),
                ficha.criadaEm(), ficha.validaAte(), itens);
        return paraDominio(jpa.save(entidade));
    }

    @Override
    public Optional<FichaDeTreino> porId(Long id) {
        return jpa.findById(id).map(FichaRepositorioJpa::paraDominio);
    }

    @Override
    public List<FichaDeTreino> porAluno(Long alunoId) {
        return jpa.findByAlunoIdOrderByCriadaEmDesc(alunoId).stream()
                .map(FichaRepositorioJpa::paraDominio).toList();
    }

    @Override
    public void excluirPorAluno(Long alunoId) {
        jpa.deleteAll(jpa.findByAlunoIdOrderByCriadaEmDesc(alunoId));
    }

    static FichaDeTreino paraDominio(FichaEntity e) {
        List<ItemDeTreino> itens = e.getItens().stream().map(i -> {
            ItemDeTreino item = new ItemDeTreino(i.getExercicio(), i.getGrupoMuscular(),
                    i.getSeries(), i.getRepeticoes(), i.getDescansoEmSegundos());
            if (i.isConcluido()) {
                item.concluir();
            }
            return item;
        }).toList();
        return new FichaDeTreino(e.getId(), e.getAlunoId(), e.getObjetivo(),
                e.getCriadaEm(), e.getValidaAte(), itens);
    }
}

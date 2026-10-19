package school.cesar.gymflow.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import school.cesar.gymflow.domain.treino.model.Exercicio;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeExercicios;
import school.cesar.gymflow.infrastructure.persistence.entity.ExercicioEntity;
import school.cesar.gymflow.infrastructure.persistence.springdata.ExercicioJpaRepository;

/** Adaptador de saida do catalogo de exercicios. */
@Repository
public class ExercicioRepositorioJpa implements RepositorioDeExercicios {

    private final ExercicioJpaRepository jpa;

    public ExercicioRepositorioJpa(ExercicioJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Exercicio salvar(Exercicio ex) {
        ExercicioEntity e = ex.id() == null ? new ExercicioEntity() : jpa.findById(ex.id()).orElseGet(ExercicioEntity::new);
        e.id = ex.id();
        e.nome = ex.nome();
        e.grupoMuscular = ex.grupoMuscular();
        e.equipamento = ex.equipamento();
        e.ativo = ex.ativo();
        return paraDominio(jpa.save(e));
    }

    @Override
    public Optional<Exercicio> porId(Long id) {
        return jpa.findById(id).map(ExercicioRepositorioJpa::paraDominio);
    }

    @Override
    public Optional<Exercicio> porNome(String nome) {
        return nome == null ? Optional.empty()
                : jpa.findFirstByNomeIgnoreCase(nome.trim()).map(ExercicioRepositorioJpa::paraDominio);
    }

    @Override
    public List<Exercicio> todos() {
        return jpa.findAllByOrderByGrupoMuscularAscNomeAsc().stream().map(ExercicioRepositorioJpa::paraDominio).toList();
    }

    @Override
    public List<Exercicio> ativos() {
        return jpa.findByAtivoTrueOrderByIdAsc().stream().map(ExercicioRepositorioJpa::paraDominio).toList();
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public long quantidade() {
        return jpa.count();
    }

    private static Exercicio paraDominio(ExercicioEntity e) {
        return new Exercicio(e.id, e.nome, e.grupoMuscular, e.equipamento, e.ativo);
    }
}

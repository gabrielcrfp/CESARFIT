package school.cesar.gymflow.infrastructure.persistence.adapter;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Repository;

import school.cesar.gymflow.domain.treino.model.Presenca;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDePresencas;
import school.cesar.gymflow.infrastructure.persistence.entity.PresencaEntity;
import school.cesar.gymflow.infrastructure.persistence.springdata.PresencaJpaRepository;

/** Adaptador de saida: traduz a Presenca para o modelo relacional. */
@Repository
public class PresencaRepositorioJpa implements RepositorioDePresencas {

    private final PresencaJpaRepository jpa;

    public PresencaRepositorioJpa(PresencaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Presenca salvar(Presenca p) {
        PresencaEntity e = jpa.save(new PresencaEntity(p.id(), p.alunoId(), p.data()));
        return new Presenca(e.getId(), e.getAlunoId(), e.getData());
    }

    @Override
    public boolean existe(Long alunoId, LocalDate data) {
        return jpa.existsByAlunoIdAndData(alunoId, data);
    }

    @Override
    public List<Presenca> porAluno(Long alunoId) {
        return jpa.findByAlunoIdOrderByDataDesc(alunoId).stream()
                .map(e -> new Presenca(e.getId(), e.getAlunoId(), e.getData()))
                .toList();
    }
}

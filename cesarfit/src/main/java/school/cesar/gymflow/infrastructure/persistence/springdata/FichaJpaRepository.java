package school.cesar.gymflow.infrastructure.persistence.springdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import school.cesar.gymflow.infrastructure.persistence.entity.FichaEntity;

public interface FichaJpaRepository extends JpaRepository<FichaEntity, Long> {

    List<FichaEntity> findByAlunoIdOrderByCriadaEmDescIdDesc(Long alunoId);

    @org.springframework.data.jpa.repository.Query(
            "select count(f) > 0 from FichaEntity f join f.itens i where lower(i.exercicio) = lower(:nome)")
    boolean usaExercicio(@org.springframework.data.repository.query.Param("nome") String nome);
}

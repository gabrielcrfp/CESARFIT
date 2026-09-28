package school.cesar.gymflow.infrastructure.persistence.springdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import school.cesar.gymflow.infrastructure.persistence.entity.FichaEntity;

public interface FichaJpaRepository extends JpaRepository<FichaEntity, Long> {

    List<FichaEntity> findByAlunoIdOrderByCriadaEmDesc(Long alunoId);
}

package school.cesar.gymflow.infrastructure.persistence.springdata;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import school.cesar.gymflow.infrastructure.persistence.entity.PresencaEntity;

public interface PresencaJpaRepository extends JpaRepository<PresencaEntity, Long> {

    boolean existsByAlunoIdAndData(Long alunoId, LocalDate data);

    List<PresencaEntity> findByAlunoIdOrderByDataDesc(Long alunoId);

    void deleteByAlunoId(Long alunoId);
}

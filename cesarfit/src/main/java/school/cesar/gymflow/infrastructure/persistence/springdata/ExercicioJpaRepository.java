package school.cesar.gymflow.infrastructure.persistence.springdata;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import school.cesar.gymflow.infrastructure.persistence.entity.ExercicioEntity;

public interface ExercicioJpaRepository extends JpaRepository<ExercicioEntity, Long> {

    Optional<ExercicioEntity> findFirstByNomeIgnoreCase(String nome);

    List<ExercicioEntity> findAllByOrderByGrupoMuscularAscNomeAsc();

    List<ExercicioEntity> findByAtivoTrueOrderByIdAsc();
}

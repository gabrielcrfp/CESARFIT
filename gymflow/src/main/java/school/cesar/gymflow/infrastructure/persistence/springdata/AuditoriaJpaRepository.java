package school.cesar.gymflow.infrastructure.persistence.springdata;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import school.cesar.gymflow.infrastructure.persistence.entity.AuditoriaEntity;

public interface AuditoriaJpaRepository extends JpaRepository<AuditoriaEntity, Long> {
    List<AuditoriaEntity> findTop200ByOrderByOcorridoEmDescIdDesc();
}

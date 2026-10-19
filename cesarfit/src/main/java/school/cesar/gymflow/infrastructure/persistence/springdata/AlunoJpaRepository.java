package school.cesar.gymflow.infrastructure.persistence.springdata;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import school.cesar.gymflow.infrastructure.persistence.entity.AlunoEntity;

public interface AlunoJpaRepository extends JpaRepository<AlunoEntity, Long> {

    Optional<AlunoEntity> findByCpf(String cpf);

    List<AlunoEntity> findAllByOrderByNomeAsc();
}

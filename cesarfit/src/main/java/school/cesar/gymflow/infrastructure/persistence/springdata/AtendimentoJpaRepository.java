package school.cesar.gymflow.infrastructure.persistence.springdata;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import school.cesar.gymflow.infrastructure.persistence.entity.AtendimentoEntity;

public interface AtendimentoJpaRepository extends JpaRepository<AtendimentoEntity, Long> {

    List<AtendimentoEntity> findByAlunoIdOrderByAbertaEmDesc(Long alunoId);

    List<AtendimentoEntity> findAllByOrderByAbertaEmDesc();
}

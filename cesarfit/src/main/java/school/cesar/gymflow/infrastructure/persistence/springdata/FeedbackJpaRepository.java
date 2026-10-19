package school.cesar.gymflow.infrastructure.persistence.springdata;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import school.cesar.gymflow.infrastructure.persistence.entity.FeedbackEntity;

public interface FeedbackJpaRepository extends JpaRepository<FeedbackEntity, Long> {

    boolean existsByAlunoIdAndData(Long alunoId, LocalDate data);

    List<FeedbackEntity> findByAlunoIdOrderByDataDesc(Long alunoId);
}

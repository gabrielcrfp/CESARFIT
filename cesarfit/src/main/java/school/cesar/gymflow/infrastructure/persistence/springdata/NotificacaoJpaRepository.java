package school.cesar.gymflow.infrastructure.persistence.springdata;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import school.cesar.gymflow.infrastructure.persistence.entity.NotificacaoEntity;

public interface NotificacaoJpaRepository extends JpaRepository<NotificacaoEntity, Long> {

    boolean existsByMatriculaIdAndVencimento(Long matriculaId, LocalDate vencimento);

    List<NotificacaoEntity> findByAlunoIdOrderByEnviadaEmDesc(Long alunoId);

    List<NotificacaoEntity> findAllByOrderByEnviadaEmDesc();
}

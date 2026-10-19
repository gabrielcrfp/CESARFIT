package school.cesar.gymflow.infrastructure.persistence.springdata;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import school.cesar.gymflow.domain.matricula.model.StatusMatricula;
import school.cesar.gymflow.infrastructure.persistence.entity.MatriculaEntity;

public interface MatriculaJpaRepository extends JpaRepository<MatriculaEntity, Long> {

    List<MatriculaEntity> findByAlunoIdOrderByInicioDesc(Long alunoId);

    List<MatriculaEntity> findAllByOrderByIdDesc();

    @Query("select m from MatriculaEntity m where m.alunoId = :alunoId "
            + "and m.status = :ativa and m.fim >= :hoje order by m.fim desc")
    List<MatriculaEntity> buscarVigentes(@Param("alunoId") Long alunoId,
                                         @Param("hoje") LocalDate hoje,
                                         @Param("ativa") StatusMatricula ativa);

    default Optional<MatriculaEntity> vigente(Long alunoId, LocalDate hoje) {
        return buscarVigentes(alunoId, hoje, StatusMatricula.ATIVA).stream().findFirst();
    }
}

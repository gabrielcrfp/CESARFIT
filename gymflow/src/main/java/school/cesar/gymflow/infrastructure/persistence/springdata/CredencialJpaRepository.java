package school.cesar.gymflow.infrastructure.persistence.springdata;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import school.cesar.gymflow.domain.acesso.model.Perfil;
import school.cesar.gymflow.infrastructure.persistence.entity.CredencialEntity;

public interface CredencialJpaRepository extends JpaRepository<CredencialEntity, Long> {

    Optional<CredencialEntity> findByLoginAndPerfil(String login, Perfil perfil);

    Optional<CredencialEntity> findByAlunoId(Long alunoId);

    void deleteByAlunoId(Long alunoId);
}

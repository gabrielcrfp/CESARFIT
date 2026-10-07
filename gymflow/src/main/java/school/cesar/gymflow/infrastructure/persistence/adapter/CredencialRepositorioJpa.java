package school.cesar.gymflow.infrastructure.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import school.cesar.gymflow.domain.acesso.model.Credencial;
import school.cesar.gymflow.domain.acesso.model.Perfil;
import school.cesar.gymflow.domain.acesso.repositorio.RepositorioDeCredenciais;
import school.cesar.gymflow.infrastructure.persistence.entity.CredencialEntity;
import school.cesar.gymflow.infrastructure.persistence.springdata.CredencialJpaRepository;

/** Adaptador de saida: traduz o agregado Credencial para o modelo relacional. */
@Repository
public class CredencialRepositorioJpa implements RepositorioDeCredenciais {

    private final CredencialJpaRepository jpa;

    public CredencialRepositorioJpa(CredencialJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Credencial salvar(Credencial c) {
        CredencialEntity salva = jpa.save(new CredencialEntity(c.id(), c.login(), c.alunoId(),
                c.perfil(), c.nome(), c.senhaCodificada()));
        return paraDominio(salva);
    }

    @Override
    public Optional<Credencial> daEquipe(String login) {
        if (login == null) {
            return Optional.empty();
        }
        return jpa.findByLoginAndPerfil(login.trim().toUpperCase(), Perfil.EQUIPE)
                .map(CredencialRepositorioJpa::paraDominio);
    }

    @Override
    public Optional<Credencial> doAluno(Long alunoId) {
        return jpa.findByAlunoId(alunoId).map(CredencialRepositorioJpa::paraDominio);
    }

    @Override
    public void excluirPorAluno(Long alunoId) {
        jpa.deleteByAlunoId(alunoId);
    }

    private static Credencial paraDominio(CredencialEntity e) {
        return new Credencial(e.getId(), e.getLogin(), e.getAlunoId(), e.getPerfil(),
                e.getNome(), e.getSenhaCodificada());
    }
}

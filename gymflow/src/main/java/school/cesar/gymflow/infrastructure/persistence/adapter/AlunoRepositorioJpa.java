package school.cesar.gymflow.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.shared.Cpf;
import school.cesar.gymflow.infrastructure.persistence.entity.AlunoEntity;
import school.cesar.gymflow.infrastructure.persistence.springdata.AlunoJpaRepository;

/** Adaptador de saida: traduz o agregado Aluno para o modelo relacional. */
@Repository
public class AlunoRepositorioJpa implements RepositorioDeAlunos {

    private final AlunoJpaRepository jpa;

    public AlunoRepositorioJpa(AlunoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Aluno salvar(Aluno aluno) {
        AlunoEntity entidade = new AlunoEntity(aluno.id(), aluno.nome(), aluno.cpf().numero(),
                aluno.telefone(), aluno.email(), aluno.dataDeNascimento(), aluno.objetivo());
        return paraDominio(jpa.save(entidade));
    }

    @Override
    public Optional<Aluno> porId(Long id) {
        return jpa.findById(id).map(AlunoRepositorioJpa::paraDominio);
    }

    @Override
    public Optional<Aluno> porCpf(Cpf cpf) {
        return jpa.findByCpf(cpf.numero()).map(AlunoRepositorioJpa::paraDominio);
    }

    @Override
    public Optional<Aluno> porEmail(String email) {
        return email == null ? Optional.empty()
                : jpa.findByEmailIgnoreCase(email.trim()).map(AlunoRepositorioJpa::paraDominio);
    }

    @Override
    public List<Aluno> todos() {
        return jpa.findAllByOrderByNomeAsc().stream().map(AlunoRepositorioJpa::paraDominio).toList();
    }

    @Override
    public long quantidade() {
        return jpa.count();
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
    }

    static Aluno paraDominio(AlunoEntity e) {
        String telefone = e.getTelefone() == null || e.getTelefone().isBlank()
                ? "00000000000" : e.getTelefone();
        return new Aluno(e.getId(), e.getNome(), Cpf.de(e.getCpf()), telefone, e.getEmail(),
                e.getDataDeNascimento(), e.getObjetivo());
    }
}

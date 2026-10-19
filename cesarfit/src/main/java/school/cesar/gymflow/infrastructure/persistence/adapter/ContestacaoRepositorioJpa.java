package school.cesar.gymflow.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import school.cesar.gymflow.domain.financeiro.model.ContestacaoDeCobranca;
import school.cesar.gymflow.domain.financeiro.repositorio.RepositorioDeContestacoes;
import school.cesar.gymflow.infrastructure.persistence.entity.ContestacaoEntity;
import school.cesar.gymflow.infrastructure.persistence.springdata.ContestacaoJpaRepository;

/** Adaptador de saida das contestacoes de cobranca. */
@Repository
public class ContestacaoRepositorioJpa implements RepositorioDeContestacoes {

    private final ContestacaoJpaRepository jpa;

    public ContestacaoRepositorioJpa(ContestacaoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public ContestacaoDeCobranca salvar(ContestacaoDeCobranca c) {
        ContestacaoEntity e = new ContestacaoEntity();
        e.id = c.id();
        e.matriculaId = c.matriculaId();
        e.alunoId = c.alunoId();
        e.indiceDoPagamento = c.indiceDoPagamento();
        e.valor = c.valor();
        e.motivo = c.motivo();
        e.status = c.status();
        e.parecer = c.parecer();
        e.abertaEm = c.abertaEm();
        e.analisadaEm = c.analisadaEm();
        return paraDominio(jpa.save(e));
    }

    @Override
    public Optional<ContestacaoDeCobranca> porId(Long id) {
        return jpa.findById(id).map(ContestacaoRepositorioJpa::paraDominio);
    }

    @Override
    public List<ContestacaoDeCobranca> porAluno(Long alunoId) {
        return jpa.findByAlunoIdOrderByAbertaEmDesc(alunoId).stream().map(ContestacaoRepositorioJpa::paraDominio).toList();
    }

    @Override
    public List<ContestacaoDeCobranca> todas() {
        return jpa.findAllByOrderByAbertaEmDesc().stream().map(ContestacaoRepositorioJpa::paraDominio).toList();
    }

    private static ContestacaoDeCobranca paraDominio(ContestacaoEntity e) {
        return new ContestacaoDeCobranca(e.id, e.matriculaId, e.alunoId, e.indiceDoPagamento, e.valor, e.motivo,
                e.status, e.parecer, e.abertaEm, e.analisadaEm);
    }
}

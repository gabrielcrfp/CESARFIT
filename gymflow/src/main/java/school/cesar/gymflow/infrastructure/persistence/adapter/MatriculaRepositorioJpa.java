package school.cesar.gymflow.infrastructure.persistence.adapter;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.model.Pagamento;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.Dinheiro;
import school.cesar.gymflow.infrastructure.persistence.entity.MatriculaEntity;
import school.cesar.gymflow.infrastructure.persistence.entity.PagamentoEmbeddable;
import school.cesar.gymflow.infrastructure.persistence.springdata.MatriculaJpaRepository;

/** Adaptador de saida do agregado Matricula. */
@Repository
public class MatriculaRepositorioJpa implements RepositorioDeMatriculas {

    private final MatriculaJpaRepository jpa;

    public MatriculaRepositorioJpa(MatriculaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Matricula salvar(Matricula matricula) {
        List<PagamentoEmbeddable> pagamentos = matricula.pagamentos().stream()
                .map(p -> new PagamentoEmbeddable(p.valor().valor(), p.data(), p.formaDePagamento(),
                        p.numeroParcela(), p.vencimento()))
                .toList();
        MatriculaEntity entidade = new MatriculaEntity(matricula.id(), matricula.alunoId(),
                matricula.modalidade(), matricula.valorMensal().valor(),
                matricula.inicio(), matricula.fim(), matricula.status(),
                matricula.justificativaCancelamento(), matricula.canceladaEm(), pagamentos);
        return paraDominio(jpa.save(entidade));
    }

    @Override
    public Optional<Matricula> porId(Long id) {
        return jpa.findById(id).map(MatriculaRepositorioJpa::paraDominio);
    }

    @Override
    public Optional<Matricula> vigentePorAluno(Long alunoId) {
        return jpa.vigente(alunoId, LocalDate.now()).map(MatriculaRepositorioJpa::paraDominio);
    }

    @Override
    public List<Matricula> porAluno(Long alunoId) {
        return jpa.findByAlunoIdOrderByInicioDesc(alunoId).stream()
                .map(MatriculaRepositorioJpa::paraDominio).toList();
    }

    @Override
    public List<Matricula> todas() {
        return jpa.findAllByOrderByIdDesc().stream()
                .map(MatriculaRepositorioJpa::paraDominio).toList();
    }

    @Override
    public void excluir(Long id) {
        jpa.deleteById(id);
    }

    static Matricula paraDominio(MatriculaEntity e) {
        List<Pagamento> pagamentos = e.getPagamentos().stream()
                .map(p -> new Pagamento(Dinheiro.de(p.getValor()), p.getData(), p.getFormaDePagamento(),
                        p.getNumeroParcela(), p.getVencimento()))
                .toList();
        Matricula dominio = new Matricula(e.getId(), e.getAlunoId(), e.getModalidade(),
                Dinheiro.de(e.getValorMensal()), e.getInicio(), e.getFim(), e.getStatus(), pagamentos);
        if (e.getStatus() == school.cesar.gymflow.domain.matricula.model.StatusMatricula.CANCELADA) {
            dominio.cancelar(e.getJustificativaCancelamento() == null ? "Nao informada" : e.getJustificativaCancelamento(),
                    e.getCanceladaEm());
        }
        return dominio;
    }
}

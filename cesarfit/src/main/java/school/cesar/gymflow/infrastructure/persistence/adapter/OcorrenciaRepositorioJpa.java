package school.cesar.gymflow.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import school.cesar.gymflow.domain.operacao.model.OcorrenciaDeEquipamento;
import school.cesar.gymflow.domain.operacao.repositorio.RepositorioDeOcorrencias;
import school.cesar.gymflow.infrastructure.persistence.entity.OcorrenciaEntity;
import school.cesar.gymflow.infrastructure.persistence.springdata.OcorrenciaJpaRepository;

/** Adaptador de saida das ocorrencias de equipamentos. */
@Repository
public class OcorrenciaRepositorioJpa implements RepositorioDeOcorrencias {

    private final OcorrenciaJpaRepository jpa;

    public OcorrenciaRepositorioJpa(OcorrenciaJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public OcorrenciaDeEquipamento salvar(OcorrenciaDeEquipamento o) {
        OcorrenciaEntity e = new OcorrenciaEntity();
        e.id = o.id();
        e.equipamento = o.equipamento();
        e.descricao = o.descricao();
        e.status = o.status();
        e.abertaEm = o.abertaEm();
        e.atualizadaEm = o.atualizadaEm();
        e.solucao = o.solucao();
        return paraDominio(jpa.save(e));
    }

    @Override
    public Optional<OcorrenciaDeEquipamento> porId(Long id) {
        return jpa.findById(id).map(OcorrenciaRepositorioJpa::paraDominio);
    }

    @Override
    public List<OcorrenciaDeEquipamento> todas() {
        return jpa.findAllByOrderByAbertaEmDesc().stream().map(OcorrenciaRepositorioJpa::paraDominio).toList();
    }

    private static OcorrenciaDeEquipamento paraDominio(OcorrenciaEntity e) {
        return new OcorrenciaDeEquipamento(e.id, e.equipamento, e.descricao, e.status, e.abertaEm,
                e.atualizadaEm, e.solucao);
    }
}

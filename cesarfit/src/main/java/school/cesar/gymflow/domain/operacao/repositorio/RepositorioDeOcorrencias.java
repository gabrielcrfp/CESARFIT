package school.cesar.gymflow.domain.operacao.repositorio;

import java.util.List;
import java.util.Optional;

import school.cesar.gymflow.domain.operacao.model.OcorrenciaDeEquipamento;

/** Porta de saida (arquitetura limpa). */
public interface RepositorioDeOcorrencias {

    OcorrenciaDeEquipamento salvar(OcorrenciaDeEquipamento ocorrencia);

    Optional<OcorrenciaDeEquipamento> porId(Long id);

    List<OcorrenciaDeEquipamento> todas();
}

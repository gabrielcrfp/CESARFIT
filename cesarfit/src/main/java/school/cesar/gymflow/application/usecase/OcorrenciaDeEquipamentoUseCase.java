package school.cesar.gymflow.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.operacao.model.OcorrenciaDeEquipamento;
import school.cesar.gymflow.domain.operacao.repositorio.RepositorioDeOcorrencias;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Funcionalidade: registro de problemas de equipamentos (aberta -> em andamento -> resolvida). */
@Service
public class OcorrenciaDeEquipamentoUseCase {

    private final RepositorioDeOcorrencias ocorrencias;
    private final PublicadorDeEventos publicador;

    public OcorrenciaDeEquipamentoUseCase(RepositorioDeOcorrencias ocorrencias, PublicadorDeEventos publicador) {
        this.ocorrencias = ocorrencias;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.OcorrenciaResumo registrar(Comandos.RegistrarOcorrencia comando) {
        OcorrenciaDeEquipamento nova = OcorrenciaDeEquipamento.registrar(comando.equipamento(), comando.descricao());
        boolean duplicada = ocorrencias.todas().stream()
                .anyMatch(o -> o.pendente() && o.equipamento().equalsIgnoreCase(nova.equipamento()));
        if (duplicada) {
            throw new RegraDeNegocioException("Ja existe uma ocorrencia pendente para o equipamento " + nova.equipamento());
        }
        return salvar(nova, "registrada: " + nova.descricao());
    }

    @Transactional
    public Respostas.OcorrenciaResumo iniciar(Long id) {
        OcorrenciaDeEquipamento o = buscar(id);
        o.iniciarAtendimento();
        return salvar(o, "em andamento");
    }

    @Transactional
    public Respostas.OcorrenciaResumo resolver(Long id, String solucao) {
        OcorrenciaDeEquipamento o = buscar(id);
        o.resolver(solucao);
        return salvar(o, "resolvida: " + o.solucao());
    }

    @Transactional(readOnly = true)
    public List<Respostas.OcorrenciaResumo> listar() {
        return ocorrencias.todas().stream().map(OcorrenciaDeEquipamentoUseCase::resumo).toList();
    }

    private OcorrenciaDeEquipamento buscar(Long id) {
        return ocorrencias.porId(id).orElseThrow(() -> new RegraDeNegocioException("Ocorrencia nao encontrada"));
    }

    private Respostas.OcorrenciaResumo salvar(OcorrenciaDeEquipamento o, String detalhe) {
        OcorrenciaDeEquipamento salva = ocorrencias.salvar(o);
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.OCORRENCIA_EQUIPAMENTO,
                String.valueOf(salva.id()), salva.equipamento() + " " + detalhe));
        return resumo(salva);
    }

    static Respostas.OcorrenciaResumo resumo(OcorrenciaDeEquipamento o) {
        return new Respostas.OcorrenciaResumo(o.id(), o.equipamento(), o.descricao(), o.status().name(),
                o.abertaEm(), o.atualizadaEm(), o.solucao());
    }
}

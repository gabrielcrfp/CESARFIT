package school.cesar.gymflow.domain.operacao.model;

import java.time.LocalDateTime;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.shared.Texto;

/** Raiz de agregado: problema registrado em um equipamento. ABERTA -> EM_ANDAMENTO -> RESOLVIDA. */
public class OcorrenciaDeEquipamento {

    private Long id;
    private final String equipamento;
    private final String descricao;
    private StatusOcorrencia status;
    private final LocalDateTime abertaEm;
    private LocalDateTime atualizadaEm;
    private String solucao;

    public OcorrenciaDeEquipamento(Long id, String equipamento, String descricao, StatusOcorrencia status,
                                   LocalDateTime abertaEm, LocalDateTime atualizadaEm, String solucao) {
        this.id = id;
        this.equipamento = Texto.obrigatorio(equipamento, 2, 80, "Equipamento");
        this.descricao = Texto.obrigatorio(descricao, 10, 500, "Descricao do problema");
        this.status = status == null ? StatusOcorrencia.ABERTA : status;
        this.abertaEm = abertaEm;
        this.atualizadaEm = atualizadaEm;
        this.solucao = solucao;
    }

    public static OcorrenciaDeEquipamento registrar(String equipamento, String descricao) {
        LocalDateTime agora = LocalDateTime.now();
        return new OcorrenciaDeEquipamento(null, equipamento, descricao, StatusOcorrencia.ABERTA, agora, agora, null);
    }

    public void iniciarAtendimento() {
        if (status != StatusOcorrencia.ABERTA) {
            throw new RegraDeNegocioException("Somente ocorrencia aberta pode entrar em andamento");
        }
        status = StatusOcorrencia.EM_ANDAMENTO;
        atualizadaEm = LocalDateTime.now();
    }

    public void resolver(String descricaoDaSolucao) {
        if (status != StatusOcorrencia.EM_ANDAMENTO) {
            throw new RegraDeNegocioException("A ocorrencia precisa estar em andamento para ser resolvida");
        }
        solucao = Texto.obrigatorio(descricaoDaSolucao, 5, 500, "Solucao");
        status = StatusOcorrencia.RESOLVIDA;
        atualizadaEm = LocalDateTime.now();
    }

    public boolean pendente() {
        return status != StatusOcorrencia.RESOLVIDA;
    }

    public void atribuirId(Long id) {
        this.id = id;
    }

    public Long id() { return id; }
    public String equipamento() { return equipamento; }
    public String descricao() { return descricao; }
    public StatusOcorrencia status() { return status; }
    public LocalDateTime abertaEm() { return abertaEm; }
    public LocalDateTime atualizadaEm() { return atualizadaEm; }
    public String solucao() { return solucao; }
}

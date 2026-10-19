package school.cesar.gymflow.application.usecase;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.financeiro.model.ContestacaoDeCobranca;
import school.cesar.gymflow.domain.financeiro.repositorio.RepositorioDeContestacoes;
import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.model.Pagamento;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Funcionalidade: contestacao de cobranca.
 * O aluno contesta um pagamento da propria matricula; a equipe registra parecer e resultado.
 * Contestacao procedente estorna o pagamento automaticamente.
 */
@Service
public class ContestacaoDeCobrancaUseCase {

    private final RepositorioDeContestacoes contestacoes;
    private final RepositorioDeMatriculas matriculas;
    private final NomesDeAlunos nomes;
    private final PublicadorDeEventos publicador;

    public ContestacaoDeCobrancaUseCase(RepositorioDeContestacoes contestacoes, RepositorioDeMatriculas matriculas,
                                        NomesDeAlunos nomes, PublicadorDeEventos publicador) {
        this.contestacoes = contestacoes;
        this.matriculas = matriculas;
        this.nomes = nomes;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.ContestacaoResumo abrir(Long alunoId, Comandos.AbrirContestacao comando) {
        Matricula matricula = matriculas.porId(comando.matriculaId())
                .filter(m -> m.alunoId().equals(alunoId))
                .orElseThrow(() -> new RegraDeNegocioException("Matricula nao encontrada para este aluno"));
        Pagamento pagamento = matricula.pagamento(comando.indiceDoPagamento());
        if (pagamento.estornado()) {
            throw new RegraDeNegocioException("Pagamento estornado nao pode ser contestado");
        }
        boolean jaAberta = contestacoes.porAluno(alunoId).stream().anyMatch(c -> c.aberta()
                && c.matriculaId().equals(matricula.id()) && c.indiceDoPagamento() == comando.indiceDoPagamento());
        if (jaAberta) {
            throw new RegraDeNegocioException("Ja existe uma contestacao em analise para este pagamento");
        }
        ContestacaoDeCobranca salva = contestacoes.salvar(ContestacaoDeCobranca.abrir(matricula.id(), alunoId,
                comando.indiceDoPagamento(), pagamento.valor().toString(), comando.motivo()));
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.CONTESTACAO_COBRANCA, String.valueOf(salva.id()),
                "Cobranca de " + salva.valor() + " contestada: " + salva.motivo()));
        return resumo(salva, nomes.de(alunoId));
    }

    @Transactional
    public Respostas.ContestacaoResumo analisar(Long id, Comandos.AnalisarContestacao comando) {
        ContestacaoDeCobranca contestacao = contestacoes.porId(id)
                .orElseThrow(() -> new RegraDeNegocioException("Contestacao nao encontrada"));
        contestacao.analisar(comando.procedente(), comando.parecer());
        if (comando.procedente()) {
            Matricula matricula = matriculas.porId(contestacao.matriculaId())
                    .orElseThrow(() -> new RegraDeNegocioException("Matricula nao encontrada"));
            String motivo = "Contestacao #" + contestacao.id() + " procedente: " + contestacao.parecer();
            matricula.estornarPagamento(contestacao.indiceDoPagamento(),
                    motivo.length() > 300 ? motivo.substring(0, 300) : motivo);
            matriculas.salvar(matricula);
        }
        ContestacaoDeCobranca salva = contestacoes.salvar(contestacao);
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.CONTESTACAO_COBRANCA, String.valueOf(salva.id()),
                "Contestacao " + salva.status() + ": " + salva.parecer()));
        return resumo(salva, nomes.de(salva.alunoId()));
    }

    @Transactional(readOnly = true)
    public List<Respostas.ContestacaoResumo> doAluno(Long alunoId) {
        String nome = nomes.de(alunoId);
        return contestacoes.porAluno(alunoId).stream().map(c -> resumo(c, nome)).toList();
    }

    @Transactional(readOnly = true)
    public List<Respostas.ContestacaoResumo> todas() {
        Map<Long, String> mapa = nomes.mapa();
        return contestacoes.todas().stream().map(c -> resumo(c, mapa.getOrDefault(c.alunoId(), "-"))).toList();
    }

    static Respostas.ContestacaoResumo resumo(ContestacaoDeCobranca c, String nome) {
        return new Respostas.ContestacaoResumo(c.id(), c.matriculaId(), c.alunoId(), nome, c.indiceDoPagamento(),
                c.valor(), c.motivo(), c.status().name(), c.parecer(), c.abertaEm(), c.analisadaEm());
    }
}

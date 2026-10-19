package school.cesar.gymflow.application.usecase;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.atendimento.model.SolicitacaoDeSubstituicao;
import school.cesar.gymflow.domain.atendimento.repositorio.RepositorioDeSolicitacoes;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.treino.model.Exercicio;
import school.cesar.gymflow.domain.treino.model.FichaDeTreino;
import school.cesar.gymflow.domain.treino.model.ItemDeTreino;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeExercicios;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFichas;

/**
 * Funcionalidade: solicitacao de substituicao de exercicio.
 * Aprovar troca o exercicio na ficha por outro ATIVO do catalogo e do mesmo grupo muscular.
 */
@Service
public class SubstituicaoDeExercicioUseCase {

    private final RepositorioDeSolicitacoes solicitacoes;
    private final RepositorioDeFichas fichas;
    private final RepositorioDeExercicios exercicios;
    private final NomesDeAlunos nomes;
    private final PublicadorDeEventos publicador;

    public SubstituicaoDeExercicioUseCase(RepositorioDeSolicitacoes solicitacoes, RepositorioDeFichas fichas,
                                          RepositorioDeExercicios exercicios, NomesDeAlunos nomes,
                                          PublicadorDeEventos publicador) {
        this.solicitacoes = solicitacoes;
        this.fichas = fichas;
        this.exercicios = exercicios;
        this.nomes = nomes;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.SubstituicaoResumo solicitar(Long alunoId, Comandos.SolicitarSubstituicao comando) {
        FichaDeTreino ficha = fichas.porId(comando.fichaId())
                .filter(f -> f.alunoId().equals(alunoId))
                .orElseThrow(() -> new RegraDeNegocioException("Ficha nao pertence ao aluno"));
        ItemDeTreino item = ficha.item(comando.posicao());
        if (item.concluido()) {
            throw new RegraDeNegocioException("Exercicio ja concluido nao pode ser substituido");
        }
        boolean pendente = solicitacoes.substituicoesDoAluno(alunoId).stream().anyMatch(s -> s.pendente()
                && s.fichaId().equals(ficha.id()) && s.posicao() == comando.posicao());
        if (pendente) {
            throw new RegraDeNegocioException("Ja existe um pedido de substituicao pendente para este exercicio");
        }
        SolicitacaoDeSubstituicao salva = solicitacoes.salvar(SolicitacaoDeSubstituicao.solicitar(
                alunoId, ficha.id(), comando.posicao(), item.exercicio(), comando.motivo()));
        publicar(salva, "solicitada: " + salva.motivo());
        return resumo(salva, nomes.de(alunoId));
    }

    @Transactional
    public Respostas.SubstituicaoResumo aprovar(Long id, Comandos.AprovarSubstituicao comando) {
        SolicitacaoDeSubstituicao s = buscar(id);
        if (!s.pendente()) {
            throw new RegraDeNegocioException("Solicitacao ja foi respondida");
        }
        if (comando.exercicioId() == null) {
            throw new RegraDeNegocioException("Escolha o exercicio substituto");
        }
        Exercicio novo = exercicios.porId(comando.exercicioId())
                .orElseThrow(() -> new RegraDeNegocioException("Exercicio substituto nao encontrado"));
        FichaDeTreino ficha = fichas.porId(s.fichaId())
                .orElseThrow(() -> new RegraDeNegocioException("Ficha nao encontrada"));
        if (!ficha.item(s.posicao()).exercicio().equalsIgnoreCase(s.exercicioAtual())) {
            throw new RegraDeNegocioException("O exercicio da ficha mudou desde o pedido; rejeite e peca um novo");
        }
        ficha.substituirExercicio(s.posicao(), novo);
        fichas.salvar(ficha);
        s.aprovar(novo.nome(), comando.observacao());
        SolicitacaoDeSubstituicao salva = solicitacoes.salvar(s);
        publicar(salva, "aprovada: " + salva.exercicioAtual() + " -> " + salva.exercicioNovo());
        return resumo(salva, nomes.de(salva.alunoId()));
    }

    @Transactional
    public Respostas.SubstituicaoResumo rejeitar(Long id, String justificativa) {
        SolicitacaoDeSubstituicao s = buscar(id);
        s.rejeitar(justificativa);
        SolicitacaoDeSubstituicao salva = solicitacoes.salvar(s);
        publicar(salva, "rejeitada: " + salva.resposta());
        return resumo(salva, nomes.de(salva.alunoId()));
    }

    @Transactional(readOnly = true)
    public List<Respostas.SubstituicaoResumo> doAluno(Long alunoId) {
        String nome = nomes.de(alunoId);
        return solicitacoes.substituicoesDoAluno(alunoId).stream().map(s -> resumo(s, nome)).toList();
    }

    @Transactional(readOnly = true)
    public List<Respostas.SubstituicaoResumo> todas() {
        Map<Long, String> mapa = nomes.mapa();
        return solicitacoes.substituicoes().stream().map(s -> resumo(s, mapa.getOrDefault(s.alunoId(), "-"))).toList();
    }

    private SolicitacaoDeSubstituicao buscar(Long id) {
        return solicitacoes.substituicaoPorId(id)
                .orElseThrow(() -> new RegraDeNegocioException("Solicitacao de substituicao nao encontrada"));
    }

    private void publicar(SolicitacaoDeSubstituicao s, String detalhe) {
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.SUBSTITUICAO_EXERCICIO, String.valueOf(s.id()),
                "Substituicao de " + s.exercicioAtual() + " " + detalhe));
    }

    private Respostas.SubstituicaoResumo resumo(SolicitacaoDeSubstituicao s, String nome) {
        String grupo = fichas.porId(s.fichaId())
                .filter(f -> s.posicao() < f.itens().size())
                .map(f -> f.itens().get(s.posicao()).grupoMuscular().name())
                .orElse(null);
        return new Respostas.SubstituicaoResumo(s.id(), s.alunoId(), nome, s.fichaId(), s.posicao(),
                s.exercicioAtual(), grupo, s.motivo(), s.status().name(), s.exercicioNovo(), s.resposta(),
                s.criadaEm());
    }
}

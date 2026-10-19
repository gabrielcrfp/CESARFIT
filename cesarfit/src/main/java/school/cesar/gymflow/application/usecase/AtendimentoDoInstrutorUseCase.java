package school.cesar.gymflow.application.usecase;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.atendimento.model.SolicitacaoDeAtendimento;
import school.cesar.gymflow.domain.atendimento.repositorio.RepositorioDeSolicitacoes;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.treino.model.FichaDeTreino;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFichas;

/** Funcionalidade: solicitacao de atendimento do instrutor (aberto -> em atendimento -> concluido). */
@Service
public class AtendimentoDoInstrutorUseCase {

    private final RepositorioDeSolicitacoes solicitacoes;
    private final RepositorioDeFichas fichas;
    private final NomesDeAlunos nomes;
    private final PublicadorDeEventos publicador;

    public AtendimentoDoInstrutorUseCase(RepositorioDeSolicitacoes solicitacoes, RepositorioDeFichas fichas,
                                         NomesDeAlunos nomes, PublicadorDeEventos publicador) {
        this.solicitacoes = solicitacoes;
        this.fichas = fichas;
        this.nomes = nomes;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.AtendimentoResumo abrir(Long alunoId, Comandos.AbrirAtendimento comando) {
        FichaDeTreino ficha = fichas.porAluno(alunoId).stream().findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Voce precisa ter uma ficha de treino para pedir atendimento"));
        if (solicitacoes.atendimentosDoAluno(alunoId).stream().anyMatch(SolicitacaoDeAtendimento::emAberto)) {
            throw new RegraDeNegocioException("Voce ja tem um pedido de atendimento em aberto");
        }
        String exercicio = comando.posicao() == null ? null : ficha.item(comando.posicao()).exercicio();
        return salvar(SolicitacaoDeAtendimento.abrir(alunoId, ficha.id(), exercicio, comando.motivo()), "aberto");
    }

    @Transactional
    public Respostas.AtendimentoResumo assumir(Long id, String instrutor) {
        SolicitacaoDeAtendimento s = buscar(id);
        s.assumir(instrutor);
        return salvar(s, "assumido por " + s.instrutor());
    }

    @Transactional
    public Respostas.AtendimentoResumo concluir(Long id, String orientacao) {
        SolicitacaoDeAtendimento s = buscar(id);
        s.concluir(orientacao);
        return salvar(s, "concluido: " + s.orientacao());
    }

    @Transactional(readOnly = true)
    public List<Respostas.AtendimentoResumo> doAluno(Long alunoId) {
        String nome = nomes.de(alunoId);
        return solicitacoes.atendimentosDoAluno(alunoId).stream().map(a -> resumo(a, nome)).toList();
    }

    @Transactional(readOnly = true)
    public List<Respostas.AtendimentoResumo> todos() {
        Map<Long, String> mapa = nomes.mapa();
        return solicitacoes.atendimentos().stream().map(a -> resumo(a, mapa.getOrDefault(a.alunoId(), "-"))).toList();
    }

    private SolicitacaoDeAtendimento buscar(Long id) {
        return solicitacoes.atendimentoPorId(id)
                .orElseThrow(() -> new RegraDeNegocioException("Pedido de atendimento nao encontrado"));
    }

    private Respostas.AtendimentoResumo salvar(SolicitacaoDeAtendimento s, String detalhe) {
        SolicitacaoDeAtendimento salva = solicitacoes.salvar(s);
        String nome = nomes.de(salva.alunoId());
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.ATENDIMENTO_INSTRUTOR, String.valueOf(salva.id()),
                "Atendimento de " + nome + " " + detalhe));
        return resumo(salva, nome);
    }

    static Respostas.AtendimentoResumo resumo(SolicitacaoDeAtendimento a, String nome) {
        return new Respostas.AtendimentoResumo(a.id(), a.alunoId(), nome, a.fichaId(), a.exercicio(), a.motivo(),
                a.status().name(), a.instrutor(), a.orientacao(), a.abertaEm(), a.concluidaEm());
    }
}

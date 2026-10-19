package school.cesar.gymflow.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.treino.model.Exercicio;
import school.cesar.gymflow.domain.treino.model.FichaDeTreino;
import school.cesar.gymflow.domain.treino.model.ItemDeTreino;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeExercicios;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFichas;

/**
 * O aluno monta o proprio treino sobre a ficha atual (divisao A/B/C): inclui exercicios ativos do
 * catalogo escolhendo series e repeticoes, ajusta a carga e remove exercicios.
 */
@Service
public class PersonalizarFichaUseCase {

    private final RepositorioDeFichas fichas;
    private final RepositorioDeExercicios exercicios;
    private final PublicadorDeEventos publicador;

    public PersonalizarFichaUseCase(RepositorioDeFichas fichas, RepositorioDeExercicios exercicios,
                                    PublicadorDeEventos publicador) {
        this.fichas = fichas;
        this.exercicios = exercicios;
        this.publicador = publicador;
    }

    @Transactional(readOnly = true)
    public List<Respostas.ExercicioResumo> catalogoAtivo() {
        return exercicios.ativos().stream()
                .map(e -> new Respostas.ExercicioResumo(e.id(), e.nome(), e.grupoMuscular().name(),
                        e.equipamento(), true, false))
                .toList();
    }

    @Transactional
    public Respostas.FichaResumo adicionar(Long alunoId, Comandos.AdicionarExercicioNaFicha comando) {
        FichaDeTreino ficha = fichaAtual(alunoId);
        if (comando.exercicioId() == null) {
            throw new RegraDeNegocioException("Escolha um exercicio do catalogo");
        }
        Exercicio exercicio = exercicios.porId(comando.exercicioId())
                .orElseThrow(() -> new RegraDeNegocioException("Exercicio nao encontrado"));
        ficha.adicionarExercicio(exercicio, comando.divisao(), comando.series(), comando.repeticoes());
        return salvar(ficha, exercicio.nome() + " incluido no treino " + comando.divisao().toUpperCase());
    }

    @Transactional
    public Respostas.FichaResumo ajustar(Long alunoId, int posicao, Comandos.AjustarItemDaFicha comando) {
        FichaDeTreino ficha = fichaAtual(alunoId);
        ficha.ajustarItem(posicao, comando.series(), comando.repeticoes());
        return salvar(ficha, ficha.item(posicao).exercicio() + " ajustado para "
                + comando.series() + "x" + comando.repeticoes());
    }

    @Transactional
    public Respostas.FichaResumo remover(Long alunoId, int posicao) {
        FichaDeTreino ficha = fichaAtual(alunoId);
        ItemDeTreino removido = ficha.removerItem(posicao);
        return salvar(ficha, removido.exercicio() + " removido do treino " + removido.divisao());
    }

    private FichaDeTreino fichaAtual(Long alunoId) {
        return fichas.porAluno(alunoId).stream().findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Voce ainda nao tem ficha de treino"));
    }

    private Respostas.FichaResumo salvar(FichaDeTreino ficha, String detalhe) {
        FichaDeTreino salva = fichas.salvar(ficha);
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.FICHA_PERSONALIZADA,
                String.valueOf(salva.id()), detalhe));
        return GerarFichaDeTreinoUseCase.montarResumo(salva);
    }
}

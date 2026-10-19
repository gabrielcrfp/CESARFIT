package school.cesar.gymflow.domain.treino.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import school.cesar.gymflow.domain.matricula.model.Objetivo;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/**
 * Raiz de agregado: FichaDeTreino.
 * Implementa Iterable expondo um Iterator proprio que percorre apenas os itens
 * ainda pendentes, sem revelar a estrutura interna da colecao (padrao Iterator).
 */
public class FichaDeTreino implements Iterable<ItemDeTreino> {

    private Long id;
    private final Long alunoId;
    private final Objetivo objetivo;
    private final LocalDate criadaEm;
    private final LocalDate validaAte;
    private final List<ItemDeTreino> itens = new ArrayList<>();

    public FichaDeTreino(Long id, Long alunoId, Objetivo objetivo,
                         LocalDate criadaEm, LocalDate validaAte, List<ItemDeTreino> itens) {
        this.id = id;
        this.alunoId = alunoId;
        this.objetivo = objetivo;
        this.criadaEm = criadaEm;
        this.validaAte = validaAte;
        if (itens != null) {
            this.itens.addAll(itens);
        }
    }

    public void concluirItem(int posicao) {
        if (posicao < 0 || posicao >= itens.size()) {
            throw new RegraDeNegocioException("Item inexistente na ficha: " + posicao);
        }
        itens.get(posicao).concluir();
    }

    /** Troca o exercicio de um item mantendo series, repeticoes e descanso. */
    public void substituirExercicio(int posicao, Exercicio novo) {
        if (posicao < 0 || posicao >= itens.size()) {
            throw new RegraDeNegocioException("Item inexistente na ficha: " + posicao);
        }
        ItemDeTreino atual = itens.get(posicao);
        if (novo == null || !novo.ativo()) {
            throw new RegraDeNegocioException("O exercicio substituto deve estar ativo no catalogo");
        }
        if (novo.grupoMuscular() != atual.grupoMuscular()) {
            throw new RegraDeNegocioException("O substituto deve trabalhar o mesmo grupo muscular ("
                    + atual.grupoMuscular() + ")");
        }
        if (novo.mesmoNome(atual.exercicio())) {
            throw new RegraDeNegocioException("O substituto deve ser diferente do exercicio atual");
        }
        itens.set(posicao, new ItemDeTreino(novo.nome(), atual.grupoMuscular(), atual.series(),
                atual.repeticoes(), atual.descansoEmSegundos(), atual.divisao()));
    }

    /* ---------- divisao A/B/C e personalizacao pelo aluno ---------- */

    public static final List<String> DIVISOES = List.of("A", "B", "C");
    public static final int MAX_SERIES = 10;
    public static final int MAX_REPETICOES = 60;
    public static final int MAX_POR_DIVISAO = 8;

    public static String divisaoValida(String divisao) {
        String d = divisao == null ? "" : divisao.trim().toUpperCase();
        if (!DIVISOES.contains(d)) {
            throw new RegraDeNegocioException("Divisao de treino invalida: use A, B ou C");
        }
        return d;
    }

    private static void validarCarga(int series, int repeticoes) {
        if (series < 1 || series > MAX_SERIES) {
            throw new RegraDeNegocioException("Series devem ficar entre 1 e " + MAX_SERIES);
        }
        if (repeticoes < 1 || repeticoes > MAX_REPETICOES) {
            throw new RegraDeNegocioException("Repeticoes devem ficar entre 1 e " + MAX_REPETICOES);
        }
    }

    /** O aluno escolhe um exercicio ativo do catalogo, a divisao, as series e as repeticoes. */
    public void adicionarExercicio(Exercicio exercicio, String divisao, int series, int repeticoes) {
        String d = divisaoValida(divisao);
        if (exercicio == null || !exercicio.ativo()) {
            throw new RegraDeNegocioException("Escolha um exercicio ativo do catalogo");
        }
        validarCarga(series, repeticoes);
        if (itens.stream().anyMatch(i -> i.divisao().equals(d) && exercicio.mesmoNome(i.exercicio()))) {
            throw new RegraDeNegocioException("Este exercicio ja esta no treino " + d);
        }
        if (itens.stream().filter(i -> i.divisao().equals(d)).count() >= MAX_POR_DIVISAO) {
            throw new RegraDeNegocioException("O treino " + d + " ja tem " + MAX_POR_DIVISAO + " exercicios");
        }
        int descanso = exercicio.grupoMuscular() == GrupoMuscular.CARDIO ? 0 : 60;
        int fimDaDivisao = 0;
        for (int i = 0; i < itens.size(); i++) {
            if (itens.get(i).divisao().compareTo(d) <= 0) {
                fimDaDivisao = i + 1;
            }
        }
        itens.add(fimDaDivisao, new ItemDeTreino(exercicio.nome(), exercicio.grupoMuscular(),
                series, repeticoes, descanso, d));
    }

    public void ajustarItem(int posicao, int series, int repeticoes) {
        ItemDeTreino atual = item(posicao);
        validarCarga(series, repeticoes);
        ItemDeTreino novo = new ItemDeTreino(atual.exercicio(), atual.grupoMuscular(), series, repeticoes,
                atual.descansoEmSegundos(), atual.divisao());
        if (atual.concluido()) {
            novo.concluir();
        }
        itens.set(posicao, novo);
    }

    public ItemDeTreino removerItem(int posicao) {
        ItemDeTreino atual = item(posicao);
        long naDivisao = itens.stream().filter(i -> i.divisao().equals(atual.divisao())).count();
        if (naDivisao <= 1) {
            throw new RegraDeNegocioException("O treino " + atual.divisao() + " precisa de pelo menos um exercicio");
        }
        return itens.remove(posicao);
    }

    public ItemDeTreino item(int posicao) {
        if (posicao < 0 || posicao >= itens.size()) {
            throw new RegraDeNegocioException("Item inexistente na ficha: " + posicao);
        }
        return itens.get(posicao);
    }

    public boolean estaCompleta() {
        return itens.stream().allMatch(ItemDeTreino::concluido);
    }

    public int totalPendentes() {
        return (int) itens.stream().filter(i -> !i.concluido()).count();
    }

    public boolean vigenteEm(LocalDate data) {
        return !data.isAfter(validaAte);
    }

    /** Padrao Iterator - itera somente os itens pendentes. */
    @Override
    public Iterator<ItemDeTreino> iterator() {
        return new IteradorDePendentes(itens);
    }

    public static class IteradorDePendentes implements Iterator<ItemDeTreino> {

        private final List<ItemDeTreino> origem;
        private int cursor = 0;

        private IteradorDePendentes(List<ItemDeTreino> origem) {
            this.origem = origem;
        }

        @Override
        public boolean hasNext() {
            while (cursor < origem.size() && origem.get(cursor).concluido()) {
                cursor++;
            }
            return cursor < origem.size();
        }

        @Override
        public ItemDeTreino next() {
            if (!hasNext()) {
                throw new NoSuchElementException("Nao ha mais itens pendentes na ficha");
            }
            return origem.get(cursor++);
        }
    }

    public void atribuirId(Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }

    public Long alunoId() {
        return alunoId;
    }

    public Objetivo objetivo() {
        return objetivo;
    }

    public LocalDate criadaEm() {
        return criadaEm;
    }

    public LocalDate validaAte() {
        return validaAte;
    }

    public List<ItemDeTreino> itens() {
        return Collections.unmodifiableList(itens);
    }
}

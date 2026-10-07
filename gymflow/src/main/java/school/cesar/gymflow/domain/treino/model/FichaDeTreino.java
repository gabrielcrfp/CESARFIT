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
    private boolean arquivada;
    private final List<ItemDeTreino> itens = new ArrayList<>();

    public FichaDeTreino(Long id, Long alunoId, Objetivo objetivo,
                         LocalDate criadaEm, LocalDate validaAte, List<ItemDeTreino> itens) {
        this(id, alunoId, objetivo, criadaEm, validaAte, false, itens);
    }

    public FichaDeTreino(Long id, Long alunoId, Objetivo objetivo,
                         LocalDate criadaEm, LocalDate validaAte, boolean arquivada,
                         List<ItemDeTreino> itens) {
        this.id = id;
        this.alunoId = alunoId;
        this.objetivo = objetivo;
        this.criadaEm = criadaEm;
        this.validaAte = validaAte;
        this.arquivada = arquivada;
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

    public boolean estaCompleta() {
        return itens.stream().allMatch(ItemDeTreino::concluido);
    }

    public int totalPendentes() {
        return (int) itens.stream().filter(i -> !i.concluido()).count();
    }

    public boolean vigenteEm(LocalDate data) {
        return !arquivada && !data.isAfter(validaAte);
    }

    public void arquivar() { this.arquivada = true; }

    public boolean arquivada() { return arquivada; }

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

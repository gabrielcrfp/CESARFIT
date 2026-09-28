package school.cesar.gymflow.domain.treino.model;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.TreeSet;

/**
 * Objeto de valor: a frequencia do aluno calculada a partir das presencas.
 * A "sequencia" (o foguinho) conta dias seguidos de presenca. Ela continua acesa
 * ate o fim do dia seguinte ao ultimo check-in.
 */
public class Frequencia {

    private final TreeSet<LocalDate> dias = new TreeSet<>();

    public Frequencia(Collection<LocalDate> datasDePresenca) {
        if (datasDePresenca != null) {
            dias.addAll(datasDePresenca);
        }
    }

    public boolean presenteEm(LocalDate data) {
        return dias.contains(data);
    }

    public int sequenciaAtual(LocalDate hoje) {
        LocalDate cursor = presenteEm(hoje) ? hoje : hoje.minusDays(1);
        int total = 0;
        while (dias.contains(cursor)) {
            total++;
            cursor = cursor.minusDays(1);
        }
        return total;
    }

    public int maiorSequencia() {
        int maior = 0;
        int atual = 0;
        LocalDate anterior = null;
        for (LocalDate dia : dias) {
            atual = anterior != null && anterior.plusDays(1).equals(dia) ? atual + 1 : 1;
            maior = Math.max(maior, atual);
            anterior = dia;
        }
        return maior;
    }

    public long presencasNoMes(YearMonth mes) {
        return dias.stream().filter(d -> YearMonth.from(d).equals(mes)).count();
    }

    public int total() {
        return dias.size();
    }
}

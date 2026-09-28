package school.cesar.gymflow.domain.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Objeto de valor (DDD tatico): quantia monetaria imutavel. */
public final class Dinheiro {

    public static final Dinheiro ZERO = new Dinheiro(BigDecimal.ZERO);

    private final BigDecimal valor;

    private Dinheiro(BigDecimal valor) {
        this.valor = valor.setScale(2, RoundingMode.HALF_UP);
    }

    public static Dinheiro de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new RegraDeNegocioException("Valor nao informado");
        }
        String limpo = valor.replace("R$", "").replace(" ", "").trim();
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        }
        try {
            return new Dinheiro(new BigDecimal(limpo));
        } catch (NumberFormatException e) {
            throw new RegraDeNegocioException("Valor invalido: " + valor);
        }
    }

    public boolean positivo() {
        return valor.signum() > 0;
    }

    public static Dinheiro de(BigDecimal valor) {
        return new Dinheiro(valor);
    }

    public Dinheiro somar(Dinheiro outro) {
        return new Dinheiro(this.valor.add(outro.valor));
    }

    public Dinheiro multiplicar(int fator) {
        return new Dinheiro(this.valor.multiply(BigDecimal.valueOf(fator)));
    }

    public Dinheiro aplicarDesconto(double percentual) {
        BigDecimal fator = BigDecimal.valueOf(1 - (percentual / 100.0));
        return new Dinheiro(this.valor.multiply(fator));
    }

    public boolean maiorOuIgual(Dinheiro outro) {
        return this.valor.compareTo(outro.valor) >= 0;
    }

    public BigDecimal valor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Dinheiro d) && valor.compareTo(d.valor) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor.doubleValue());
    }

    @Override
    public String toString() {
        return "R$ " + valor.toPlainString().replace(".", ",");
    }
}

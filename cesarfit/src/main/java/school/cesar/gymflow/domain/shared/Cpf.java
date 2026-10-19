package school.cesar.gymflow.domain.shared;

import java.util.Objects;

/** Objeto de valor: CPF normalizado e auto validado. */
public final class Cpf {

    private final String numero;

    private Cpf(String numero) {
        this.numero = numero;
    }

    public static Cpf de(String bruto) {
        if (bruto == null) {
            throw new RegraDeNegocioException("CPF nao informado");
        }
        String digitos = bruto.replaceAll("\\D", "");
        if (digitos.length() != 11) {
            throw new RegraDeNegocioException("CPF deve conter 11 digitos: " + bruto);
        }
        return new Cpf(digitos);
    }

    public String numero() {
        return numero;
    }

    public String formatado() {
        return numero.substring(0, 3) + "." + numero.substring(3, 6) + "."
                + numero.substring(6, 9) + "-" + numero.substring(9);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Cpf c) && numero.equals(c.numero);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero);
    }

    @Override
    public String toString() {
        return formatado();
    }
}

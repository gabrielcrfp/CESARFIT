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
        if (digitos.chars().distinct().count() == 1) {
            throw new RegraDeNegocioException("CPF invalido: todos os digitos sao iguais");
        }
        int primeiro = digitoVerificador(digitos, 9, 10);
        int segundo = digitoVerificador(digitos, 10, 11);
        if (primeiro != Character.digit(digitos.charAt(9), 10)
                || segundo != Character.digit(digitos.charAt(10), 10)) {
            throw new RegraDeNegocioException("CPF invalido: digitos verificadores incorretos");
        }
        return new Cpf(digitos);
    }

    private static int digitoVerificador(String cpf, int quantidade, int pesoInicial) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += Character.digit(cpf.charAt(i), 10) * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
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

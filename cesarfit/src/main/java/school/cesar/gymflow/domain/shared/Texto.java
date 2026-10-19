package school.cesar.gymflow.domain.shared;

/** Validacoes de texto reutilizadas pelos agregados. */
public final class Texto {

    private Texto() {
    }

    public static String obrigatorio(String valor, int minimo, int maximo, String campo) {
        String limpo = valor == null ? "" : valor.trim();
        if (limpo.length() < minimo) {
            throw new RegraDeNegocioException(minimo <= 1
                    ? campo + " e obrigatorio"
                    : campo + " deve ter pelo menos " + minimo + " caracteres");
        }
        if (limpo.length() > maximo) {
            throw new RegraDeNegocioException(campo + " deve ter no maximo " + maximo + " caracteres");
        }
        return limpo;
    }

    public static String opcional(String valor, int maximo, String campo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return obrigatorio(valor, 1, maximo, campo);
    }
}

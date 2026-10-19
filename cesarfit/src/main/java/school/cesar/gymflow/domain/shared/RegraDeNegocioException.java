package school.cesar.gymflow.domain.shared;

/** Excecao de dominio: violacao de invariante ou de regra de negocio. */
public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}

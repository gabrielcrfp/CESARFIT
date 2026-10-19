package school.cesar.gymflow.domain.acesso.servico;

/** Porta: o dominio nao conhece o algoritmo usado para proteger a senha. */
public interface CodificadorDeSenha {

    String codificar(String senha);

    boolean confere(String senha, String senhaCodificada);
}

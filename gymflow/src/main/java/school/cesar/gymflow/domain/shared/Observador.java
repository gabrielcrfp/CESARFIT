package school.cesar.gymflow.domain.shared;

/** Padrao Observer - contrato do observador. */
public interface Observador {

    /** Indica se este observador processa o tipo de evento informado. */
    boolean interessadoEm(String tipoDeEvento);

    void notificar(EventoDeDominio evento);
}

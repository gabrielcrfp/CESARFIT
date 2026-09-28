package school.cesar.gymflow.domain.shared;

import java.util.ArrayList;
import java.util.List;

/** Padrao Observer - sujeito observavel. */
public class PublicadorDeEventos {

    private final List<Observador> observadores = new ArrayList<>();

    public PublicadorDeEventos(List<Observador> observadores) {
        if (observadores != null) {
            this.observadores.addAll(observadores);
        }
    }

    public void registrar(Observador observador) {
        observadores.add(observador);
    }

    public void remover(Observador observador) {
        observadores.remove(observador);
    }

    public void publicar(EventoDeDominio evento) {
        for (Observador observador : observadores) {
            if (observador.interessadoEm(evento.tipo())) {
                observador.notificar(evento);
            }
        }
    }
}

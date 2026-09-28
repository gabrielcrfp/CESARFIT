package school.cesar.gymflow.domain.shared;

import java.util.List;

/** Porta de leitura da trilha de eventos publicados pelo dominio. */
public interface RegistroDeEventos {

    List<EventoDeDominio> ultimos(int limite);
}

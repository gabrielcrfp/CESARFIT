package school.cesar.gymflow.infrastructure.observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.Observador;
import school.cesar.gymflow.domain.shared.RegistroDeEventos;

/** Padrao Observer - observador concreto: registra todos os eventos. */
@Component
public class TrilhaDeAuditoria implements Observador, RegistroDeEventos {

    private static final int CAPACIDADE = 200;

    private final List<EventoDeDominio> registros = Collections.synchronizedList(new ArrayList<>());

    @Override
    public boolean interessadoEm(String tipoDeEvento) {
        return true;
    }

    @Override
    public void notificar(EventoDeDominio evento) {
        synchronized (registros) {
            registros.add(evento);
            if (registros.size() > CAPACIDADE) {
                registros.remove(0);
            }
        }
    }

    @Override
    public List<EventoDeDominio> ultimos(int limite) {
        synchronized (registros) {
            List<EventoDeDominio> copia = new ArrayList<>(registros);
            Collections.reverse(copia);
            return copia.size() > limite ? copia.subList(0, limite) : copia;
        }
    }

    public void limpar() {
        registros.clear();
    }
}

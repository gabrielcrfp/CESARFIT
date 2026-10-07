package school.cesar.gymflow.infrastructure.observer;

import java.util.List;

import org.springframework.stereotype.Component;

import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.Observador;
import school.cesar.gymflow.domain.shared.RegistroDeEventos;
import school.cesar.gymflow.infrastructure.persistence.entity.AuditoriaEntity;
import school.cesar.gymflow.infrastructure.persistence.springdata.AuditoriaJpaRepository;

/** Padrao Observer - observador concreto: registra todos os eventos. */
@Component
public class TrilhaDeAuditoria implements Observador, RegistroDeEventos {

    private final AuditoriaJpaRepository repositorio;

    public TrilhaDeAuditoria(AuditoriaJpaRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean interessadoEm(String tipoDeEvento) {
        return true;
    }

    @Override
    public void notificar(EventoDeDominio evento) {
        repositorio.save(new AuditoriaEntity(evento.tipo(), evento.referencia(),
                evento.detalhe(), evento.ocorridoEm()));
    }

    @Override
    public List<EventoDeDominio> ultimos(int limite) {
        int quantidade = Math.max(0, Math.min(limite, 200));
        return repositorio.findTop200ByOrderByOcorridoEmDescIdDesc().stream()
                .limit(quantidade)
                .map(e -> new EventoDeDominio(e.getTipo(), e.getReferencia(),
                        e.getDetalhe(), e.getOcorridoEm()))
                .toList();
    }
}

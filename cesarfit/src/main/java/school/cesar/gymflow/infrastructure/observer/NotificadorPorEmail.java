package school.cesar.gymflow.infrastructure.observer;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.Observador;

/** Padrao Observer - observador concreto: avisa o aluno por e-mail. */
@Component
public class NotificadorPorEmail implements Observador {

    private static final Logger LOG = LoggerFactory.getLogger(NotificadorPorEmail.class);

    private static final Set<String> INTERESSES = Set.of(
            EventoDeDominio.MATRICULA_EFETIVADA,
            EventoDeDominio.MATRICULA_RENOVADA,
            EventoDeDominio.MATRICULA_CANCELADA,
            EventoDeDominio.FICHA_GERADA,
            EventoDeDominio.MATRICULA_A_VENCER,
            EventoDeDominio.PAGAMENTO_ESTORNADO,
            EventoDeDominio.AVALIACAO_FISICA);

    @Override
    public boolean interessadoEm(String tipoDeEvento) {
        return INTERESSES.contains(tipoDeEvento);
    }

    @Override
    public void notificar(EventoDeDominio evento) {
        LOG.info("[E-MAIL] {} (ref {}): {}", evento.tipo(), evento.referencia(), evento.detalhe());
    }
}

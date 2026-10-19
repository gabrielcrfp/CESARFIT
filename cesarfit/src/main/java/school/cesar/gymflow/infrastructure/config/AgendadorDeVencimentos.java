package school.cesar.gymflow.infrastructure.config;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import school.cesar.gymflow.application.usecase.NotificacaoDeVencimentoUseCase;

/** Dispara a verificacao automatica de vencimentos todos os dias as 8h. */
@Configuration
@EnableScheduling
public class AgendadorDeVencimentos {

    private static final Logger LOG = LoggerFactory.getLogger(AgendadorDeVencimentos.class);

    private final NotificacaoDeVencimentoUseCase notificacoes;

    public AgendadorDeVencimentos(NotificacaoDeVencimentoUseCase notificacoes) {
        this.notificacoes = notificacoes;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void verificarDiariamente() {
        int enviados = notificacoes.verificar(LocalDate.now()).avisosEnviados();
        LOG.info("Verificacao de vencimentos: {} aviso(s) enviado(s)", enviados);
    }
}

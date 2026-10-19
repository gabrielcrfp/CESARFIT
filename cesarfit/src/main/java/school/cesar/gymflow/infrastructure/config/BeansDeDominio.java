package school.cesar.gymflow.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.gymflow.domain.matricula.politica.PrecoMensal;
import school.cesar.gymflow.domain.matricula.politica.PrecoAnual;
import school.cesar.gymflow.domain.matricula.politica.SeletorDePolitica;
import school.cesar.gymflow.domain.shared.Observador;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;

/**
 * A camada de dominio nao conhece o Spring; as instancias sao montadas aqui,
 * na infraestrutura (principio da inversao de dependencia).
 */
@Configuration
public class BeansDeDominio {

    @Bean
    public PublicadorDeEventos publicadorDeEventos(List<Observador> observadores) {
        return new PublicadorDeEventos(observadores);
    }

    /** Somente dois planos: Mensal (R$ 120,00) e Anual (R$ 1320,00). */
    @Bean
    public SeletorDePolitica seletorDePolitica() {
        return new SeletorDePolitica(List.of(new PrecoMensal(), new PrecoAnual()));
    }
}

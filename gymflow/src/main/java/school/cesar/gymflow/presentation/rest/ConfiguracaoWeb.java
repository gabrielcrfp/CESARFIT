package school.cesar.gymflow.presentation.rest;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Registra o filtro de sessao em toda a API, exceto no login. */
@Configuration
public class ConfiguracaoWeb implements WebMvcConfigurer {

    private final FiltroDeSessao filtroDeSessao;

    public ConfiguracaoWeb(FiltroDeSessao filtroDeSessao) {
        this.filtroDeSessao = filtroDeSessao;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registro) {
        registro.addInterceptor(filtroDeSessao)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/acesso/login");
    }
}

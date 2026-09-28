package school.cesar.gymflow.presentation.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.application.usecase.ServicoDeConsultas;

/** Endpoints de apoio da interface grafica: planos, dashboard e trilha de eventos. */
@RestController
@RequestMapping("/api")
public class PainelController {

    private final ServicoDeConsultas consultas;

    public PainelController(ServicoDeConsultas consultas) {
        this.consultas = consultas;
    }

    @GetMapping("/planos")
    public List<Respostas.PlanoResumo> planos() {
        return consultas.planos();
    }

    @GetMapping("/painel")
    public Respostas.Painel painel() {
        return consultas.painel();
    }

    @GetMapping("/eventos")
    public List<Respostas.EventoResumo> eventos() {
        return consultas.eventos();
    }
}

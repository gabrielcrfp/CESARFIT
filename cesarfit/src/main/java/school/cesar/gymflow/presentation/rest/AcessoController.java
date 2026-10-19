package school.cesar.gymflow.presentation.rest;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.application.usecase.EntrarNoSistemaUseCase;
import school.cesar.gymflow.application.usecase.GerenciadorDeSessoes;

/** Login e logout (adaptador de entrada). O token vai no corpo e tambem num cookie. */
@RestController
@RequestMapping("/api/acesso")
public class AcessoController {

    private final EntrarNoSistemaUseCase entrar;
    private final GerenciadorDeSessoes sessoes;

    public AcessoController(EntrarNoSistemaUseCase entrar, GerenciadorDeSessoes sessoes) {
        this.entrar = entrar;
        this.sessoes = sessoes;
    }

    @PostMapping("/login")
    public ResponseEntity<Respostas.Sessao> login(@RequestBody Comandos.Entrar comando) {
        Respostas.Sessao sessao = entrar.executar(comando);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie(sessao.token(), 8 * 60 * 60).toString())
                .body(sessao);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest requisicao) {
        sessoes.encerrar(FiltroDeSessao.token(requisicao));
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie("", 0).toString())
                .build();
    }

    private static ResponseCookie cookie(String valor, long segundos) {
        return ResponseCookie.from(FiltroDeSessao.COOKIE, valor)
                .httpOnly(true).sameSite("Lax").path("/").maxAge(segundos).build();
    }
}

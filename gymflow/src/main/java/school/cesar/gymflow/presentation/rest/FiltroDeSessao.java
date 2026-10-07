package school.cesar.gymflow.presentation.rest;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import school.cesar.gymflow.application.usecase.GerenciadorDeSessoes;
import school.cesar.gymflow.domain.acesso.model.Perfil;

/**
 * Protege a API: /api/area-aluno/** so para alunos logados; o restante de /api/** so para a equipe.
 * O token da sessao chega no cabecalho X-Sessao.
 */
@Component
public class FiltroDeSessao implements HandlerInterceptor {

    public static final String CABECALHO = "X-Sessao";
    public static final String ATRIBUTO = "sessao";
    public static final String COOKIE = "GYMFLOW_SESSAO";

    private final GerenciadorDeSessoes sessoes;

    public FiltroDeSessao(GerenciadorDeSessoes sessoes) {
        this.sessoes = sessoes;
    }

    @Override
    public boolean preHandle(HttpServletRequest requisicao, HttpServletResponse resposta, Object handler)
            throws IOException {
        if ("OPTIONS".equalsIgnoreCase(requisicao.getMethod())) {
            return true;
        }
        var sessao = sessoes.buscar(token(requisicao)).orElse(null);
        if (sessao == null) {
            return negar(requisicao, resposta, HttpServletResponse.SC_UNAUTHORIZED, "Sessao expirada. Entre novamente.");
        }
        boolean areaDoAluno = requisicao.getRequestURI().startsWith("/api/area-aluno");
        Perfil exigido = areaDoAluno ? Perfil.ALUNO : Perfil.EQUIPE;
        if (!requisicao.getRequestURI().startsWith("/api/acesso") && sessao.perfil() != exigido) {
            return negar(requisicao, resposta, HttpServletResponse.SC_FORBIDDEN, "Acesso nao permitido para o seu perfil");
        }
        requisicao.setAttribute(ATRIBUTO, sessao);
        return true;
    }

    /** O token vem no cabecalho X-Sessao ou, se faltar, no cookie gravado no login. */
    public static String token(HttpServletRequest requisicao) {
        String token = requisicao.getHeader(CABECALHO);
        if (token != null && !token.isBlank()) {
            return token;
        }
        if (requisicao.getCookies() != null) {
            for (var cookie : requisicao.getCookies()) {
                if (COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private static boolean negar(HttpServletRequest requisicao, HttpServletResponse resposta,
                                 int status, String mensagem) throws IOException {
        if (requisicao.getHeader("Accept") != null
                && requisicao.getHeader("Accept").contains("text/html")) {
            resposta.sendRedirect("/");
            return false;
        }
        resposta.setStatus(status);
        resposta.setContentType("application/json;charset=UTF-8");
        resposta.getWriter().write("{\"erro\":\"" + mensagem + "\"}");
        return false;
    }
}

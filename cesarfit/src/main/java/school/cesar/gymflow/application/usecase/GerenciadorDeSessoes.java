package school.cesar.gymflow.application.usecase;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import school.cesar.gymflow.domain.acesso.model.Perfil;

/** Mantem as sessoes abertas apos o login (token -> quem entrou). Validas por 8 horas. */
@Component
public class GerenciadorDeSessoes {

    private static final int HORAS_DE_VALIDADE = 8;

    public record SessaoAtiva(String token, Perfil perfil, Long alunoId, String nome, LocalDateTime expiraEm) {
    }

    private final Map<String, SessaoAtiva> sessoes = new ConcurrentHashMap<>();

    public SessaoAtiva abrir(Perfil perfil, Long alunoId, String nome) {
        String token = UUID.randomUUID().toString();
        SessaoAtiva sessao = new SessaoAtiva(token, perfil, alunoId, nome,
                LocalDateTime.now().plusHours(HORAS_DE_VALIDADE));
        sessoes.put(token, sessao);
        return sessao;
    }

    public Optional<SessaoAtiva> buscar(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        SessaoAtiva sessao = sessoes.get(token);
        if (sessao == null) {
            return Optional.empty();
        }
        if (LocalDateTime.now().isAfter(sessao.expiraEm())) {
            sessoes.remove(token);
            return Optional.empty();
        }
        return Optional.of(sessao);
    }

    public void encerrar(String token) {
        if (token != null) {
            sessoes.remove(token);
        }
    }
}

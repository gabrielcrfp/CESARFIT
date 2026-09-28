package school.cesar.gymflow.infrastructure.seguranca;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.stereotype.Component;

import school.cesar.gymflow.domain.acesso.servico.CodificadorDeSenha;

/** Adaptador: SHA-256 com sal aleatorio. Formato salvo: "sal:hash". */
@Component
public class CodificadorSha256 implements CodificadorDeSenha {

    private final SecureRandom aleatorio = new SecureRandom();

    @Override
    public String codificar(String senha) {
        byte[] sal = new byte[16];
        aleatorio.nextBytes(sal);
        String salTexto = Base64.getEncoder().encodeToString(sal);
        return salTexto + ":" + hash(salTexto, senha);
    }

    @Override
    public boolean confere(String senha, String senhaCodificada) {
        if (senha == null || senhaCodificada == null || !senhaCodificada.contains(":")) {
            return false;
        }
        String[] partes = senhaCodificada.split(":", 2);
        return MessageDigest.isEqual(
                hash(partes[0], senha).getBytes(StandardCharsets.UTF_8),
                partes[1].getBytes(StandardCharsets.UTF_8));
    }

    private static String hash(String sal, String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest((sal + senha).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponivel", e);
        }
    }
}

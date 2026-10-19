package school.cesar.gymflow.presentation.rest;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Traduz excecoes em respostas HTTP com mensagens compreensiveis para o usuario. */
@RestControllerAdvice
public class TratadorDeErros {

    private static final Logger LOG = LoggerFactory.getLogger(TratadorDeErros.class);

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<Map<String, Object>> regraDeNegocio(RegraDeNegocioException excecao) {
        return resposta(HttpStatus.UNPROCESSABLE_ENTITY, excecao.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> corpoInvalido(HttpMessageNotReadableException excecao) {
        return resposta(HttpStatus.BAD_REQUEST,
                "Dados invalidos: verifique datas, numeros e opcoes selecionadas");
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<Map<String, Object>> parametroInvalido(Exception excecao) {
        return resposta(HttpStatus.BAD_REQUEST, "Parametro invalido na requisicao");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> naoEncontrado(NoResourceFoundException excecao) {
        return resposta(HttpStatus.NOT_FOUND, "Recurso nao encontrado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> inesperado(Exception excecao) {
        LOG.error("Erro inesperado", excecao);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro inesperado no servidor. Tente novamente.");
    }

    private static ResponseEntity<Map<String, Object>> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of(
                "erro", mensagem,
                "momento", LocalDateTime.now().toString()));
    }
}

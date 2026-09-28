package school.cesar.gymflow.domain.shared;

import java.time.LocalDateTime;

/** Evento de dominio publicado apos uma mudanca relevante em um agregado. */
public record EventoDeDominio(String tipo, String referencia, String detalhe, LocalDateTime ocorridoEm) {

    public static final String MATRICULA_EFETIVADA = "MATRICULA_EFETIVADA";
    public static final String MATRICULA_RENOVADA = "MATRICULA_RENOVADA";
    public static final String MATRICULA_CANCELADA = "MATRICULA_CANCELADA";
    public static final String PAGAMENTO_REGISTRADO = "PAGAMENTO_REGISTRADO";
    public static final String MATRICULA_ALTERADA = "MATRICULA_ALTERADA";
    public static final String MATRICULA_EXCLUIDA = "MATRICULA_EXCLUIDA";
    public static final String ALUNO_CADASTRADO = "ALUNO_CADASTRADO";
    public static final String ALUNO_ATUALIZADO = "ALUNO_ATUALIZADO";
    public static final String ALUNO_EXCLUIDO = "ALUNO_EXCLUIDO";
    public static final String FICHA_GERADA = "FICHA_GERADA";
    public static final String PRESENCA_REGISTRADA = "PRESENCA_REGISTRADA";
    public static final String SENHA_ALTERADA = "SENHA_ALTERADA";

    public static EventoDeDominio de(String tipo, String referencia, String detalhe) {
        return new EventoDeDominio(tipo, referencia, detalhe, LocalDateTime.now());
    }
}

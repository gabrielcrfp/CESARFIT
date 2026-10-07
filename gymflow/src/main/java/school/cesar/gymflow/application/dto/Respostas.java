package school.cesar.gymflow.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Respostas dos casos de uso (camada de aplicacao). */
public final class Respostas {

    private Respostas() {
    }

    public record AlunoResumo(Long id, String nome, String cpf, String telefone, String email,
                              LocalDate dataDeNascimento, int idade, String objetivo,
                              boolean possuiMatriculaAtiva, int totalDeMatriculas) {
    }

    public record AlunoDetalhe(AlunoResumo aluno, List<MatriculaResumo> matriculas) {
    }

    public record PlanoResumo(String codigo, String nome, String valor, BigDecimal valorNumerico,
                              String duracao) {
    }

    public record PagamentoResumo(String valor, LocalDate data, String formaDePagamento,
                                  Integer numeroParcela, LocalDate vencimento) {
        public PagamentoResumo(String valor, LocalDate data, String formaDePagamento) {
            this(valor, data, formaDePagamento, null, null);
        }
    }

    public record MatriculaResumo(Long id, Long alunoId, String alunoNome, String plano,
                                  String planoNome, String valorMensal, BigDecimal valorNumerico,
                                  LocalDate inicio, LocalDate fim, String status, String totalPago,
                                  List<PagamentoResumo> pagamentos, String statusFinanceiro) {
    }

    public record Painel(long totalDeAlunos, long matriculasAtivas, long matriculasVencidas,
                         long matriculasCanceladas, long semanaisAtivas, long mensaisAtivas,
                         String receitaDasAtivas, long alunosSemMatriculaAtiva,
                         long presencasNoMes, long fichasAtivas, long matriculasEmAtraso,
                         List<MatriculaResumo> ultimasMatriculas) {
    }

    public record ItemResumo(int posicao, String exercicio, String grupoMuscular,
                             int series, int repeticoes, int descansoEmSegundos, boolean concluido) {
    }

    public record FichaResumo(Long id, Long alunoId, String objetivo, LocalDate criadaEm,
                              LocalDate validaAte, int pendentes, List<ItemResumo> itens) {
    }

    public record EventoResumo(String tipo, String referencia, String detalhe, String momento) {
    }

    public record Sessao(String token, String perfil, String nome, Long alunoId) {
    }

    public record DiaDeFrequencia(LocalDate data, String rotulo, boolean presente, boolean hoje) {
    }

    public record FrequenciaResumo(int sequenciaAtual, int maiorSequencia, long presencasNoMes,
                                   int totalDePresencas, boolean presenteHoje,
                                   List<DiaDeFrequencia> semana) {
    }

    public record AreaDoAluno(AlunoResumo aluno, MatriculaResumo matricula, FichaResumo ficha,
                              FrequenciaResumo frequencia) {
    }
}

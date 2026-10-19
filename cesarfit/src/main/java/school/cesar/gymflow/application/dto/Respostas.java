package school.cesar.gymflow.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    public record PagamentoResumo(int indice, String valor, LocalDate data, String formaDePagamento,
                                  boolean estornado, String motivoDoEstorno, LocalDate dataDoEstorno) {
    }

    public record MatriculaResumo(Long id, Long alunoId, String alunoNome, String plano,
                                  String planoNome, String valorMensal, BigDecimal valorNumerico,
                                  LocalDate inicio, LocalDate fim, String status, String totalPago,
                                  List<PagamentoResumo> pagamentos, String justificativaDoCancelamento,
                                  LocalDate dataDaSolicitacaoDeCancelamento, String taxaDeCancelamento) {
    }

    public record Painel(long totalDeAlunos, long matriculasAtivas, long matriculasVencidas,
                         long matriculasCanceladas, long mensaisAtivas, long anuaisAtivas,
                         String receitaDasAtivas, long alunosSemMatriculaAtiva,
                         List<MatriculaResumo> ultimasMatriculas) {
    }

    public record ItemResumo(int posicao, String exercicio, String grupoMuscular,
                             int series, int repeticoes, int descansoEmSegundos, boolean concluido,
                             String divisao) {
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

    public record ExercicioResumo(Long id, String nome, String grupoMuscular, String equipamento,
                                  boolean ativo, boolean usadoEmFichas) {
    }

    public record NotificacaoResumo(Long id, Long matriculaId, Long alunoId, String alunoNome, LocalDate vencimento,
                                    LocalDateTime enviadaEm, String mensagem, boolean lida) {
    }

    public record VerificacaoDeVencimentos(int avisosEnviados, List<NotificacaoResumo> avisos) {
    }

    public record OcorrenciaResumo(Long id, String equipamento, String descricao, String status,
                                   LocalDateTime abertaEm, LocalDateTime atualizadaEm, String solucao) {
    }

    public record ContestacaoResumo(Long id, Long matriculaId, Long alunoId, String alunoNome, int indiceDoPagamento,
                                    String valor, String motivo, String status, String parecer,
                                    LocalDateTime abertaEm, LocalDateTime analisadaEm) {
    }

    public record AvaliacaoResumo(Long id, Long alunoId, String alunoNome, LocalDateTime dataDesejada,
                                  LocalDateTime dataProposta, LocalDateTime dataAgendada, String status,
                                  String observacao, boolean emAberto, LocalDateTime criadaEm) {
    }

    public record SubstituicaoResumo(Long id, Long alunoId, String alunoNome, Long fichaId, int posicao,
                                     String exercicioAtual, String grupoMuscular, String motivo, String status,
                                     String exercicioNovo, String resposta, LocalDateTime criadaEm) {
    }

    public record AtendimentoResumo(Long id, Long alunoId, String alunoNome, Long fichaId, String exercicio,
                                    String motivo, String status, String instrutor, String orientacao,
                                    LocalDateTime abertaEm, LocalDateTime concluidaEm) {
    }

    public record FeedbackResumo(Long id, Long alunoId, LocalDate data, int esforco, String observacao,
                                 int exerciciosConcluidos, int totalDeExercicios) {
    }

    public record SolicitacoesDoAluno(List<NotificacaoResumo> avisos, List<FeedbackResumo> feedbacks,
                                      boolean feedbackDeHojeRegistrado, List<AvaliacaoResumo> avaliacoes,
                                      List<SubstituicaoResumo> substituicoes, List<AtendimentoResumo> atendimentos,
                                      List<ContestacaoResumo> contestacoes) {
    }
}

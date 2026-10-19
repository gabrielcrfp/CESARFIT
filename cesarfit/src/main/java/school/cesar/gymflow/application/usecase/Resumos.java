package school.cesar.gymflow.application.usecase;

import java.time.LocalDate;
import java.util.List;

import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.model.Matricula;

/** Converte agregados do dominio em respostas para a interface (uso interno da aplicacao). */
final class Resumos {

    private Resumos() {
    }

    static Respostas.AlunoResumo aluno(Aluno a, List<Matricula> matriculasDoAluno) {
        boolean ativa = matriculasDoAluno.stream().anyMatch(Matricula::ativa);
        return new Respostas.AlunoResumo(a.id(), a.nome(), a.cpf().formatado(), a.telefoneFormatado(),
                a.email(), a.dataDeNascimento(), a.idade(), a.objetivo().name(), ativa,
                matriculasDoAluno.size());
    }

    static Respostas.MatriculaResumo matricula(Matricula m, String alunoNome) {
        m.atualizarVigencia(LocalDate.now());
        List<Respostas.PagamentoResumo> pagamentos = new java.util.ArrayList<>();
        for (int i = 0; i < m.pagamentos().size(); i++) {
            var p = m.pagamentos().get(i);
            pagamentos.add(new Respostas.PagamentoResumo(i, p.valor().toString(), p.data(), p.formaDePagamento(),
                    p.estornado(), p.motivoDoEstorno(), p.dataDoEstorno()));
        }
        return new Respostas.MatriculaResumo(m.id(), m.alunoId(), alunoNome, m.modalidade().name(),
                m.descricaoDoPlano(), m.valorMensal().toString(), m.valorMensal().valor(),
                m.inicio(), m.fim(), m.status().name(), m.totalPago().toString(), pagamentos,
                m.justificativaDoCancelamento(), m.dataDaSolicitacaoDeCancelamento(),
                m.taxaDeCancelamento() == null ? null : m.taxaDeCancelamento().toString());
    }
}

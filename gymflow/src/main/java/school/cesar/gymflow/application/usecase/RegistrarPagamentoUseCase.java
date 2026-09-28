package school.cesar.gymflow.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.model.Pagamento;
import school.cesar.gymflow.domain.matricula.model.Aluno;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeAlunos;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.Dinheiro;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Caso de uso: registrar pagamento de mensalidade (complexidade media). */
@Service
public class RegistrarPagamentoUseCase {

    private final RepositorioDeMatriculas repositorioDeMatriculas;
    private final RepositorioDeAlunos repositorioDeAlunos;
    private final PublicadorDeEventos publicador;

    public RegistrarPagamentoUseCase(RepositorioDeMatriculas repositorioDeMatriculas,
            RepositorioDeAlunos repositorioDeAlunos,
                                     PublicadorDeEventos publicador) {
        this.repositorioDeMatriculas = repositorioDeMatriculas;
        this.repositorioDeAlunos = repositorioDeAlunos;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.MatriculaResumo executar(Comandos.RegistrarPagamento comando) {
        Matricula matricula = repositorioDeMatriculas.porId(comando.matriculaId())
                .orElseThrow(() -> new RegraDeNegocioException("Matricula nao encontrada"));

        matricula.registrarPagamento(
                Pagamento.novo(Dinheiro.de(comando.valor()), comando.formaDePagamento()));
        Matricula salva = repositorioDeMatriculas.salvar(matricula);

        publicador.publicar(EventoDeDominio.de(EventoDeDominio.PAGAMENTO_REGISTRADO,
                String.valueOf(salva.id()), "Pagamento de " + comando.valor()
                        + " via " + comando.formaDePagamento()));

        String nome = repositorioDeAlunos.porId(salva.alunoId()).map(Aluno::nome).orElse("-");
        return Resumos.matricula(salva, nome);
    }
}

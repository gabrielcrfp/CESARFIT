package school.cesar.gymflow.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.matricula.model.Matricula;
import school.cesar.gymflow.domain.matricula.model.Pagamento;
import school.cesar.gymflow.domain.matricula.repositorio.RepositorioDeMatriculas;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;

/** Caso de uso: estornar um pagamento identificado, com motivo, impedindo estorno duplicado. */
@Service
public class EstornarPagamentoUseCase {

    private final RepositorioDeMatriculas matriculas;
    private final NomesDeAlunos nomes;
    private final PublicadorDeEventos publicador;

    public EstornarPagamentoUseCase(RepositorioDeMatriculas matriculas, NomesDeAlunos nomes,
                                    PublicadorDeEventos publicador) {
        this.matriculas = matriculas;
        this.nomes = nomes;
        this.publicador = publicador;
    }

    @Transactional
    public Respostas.MatriculaResumo executar(Long matriculaId, int indiceDoPagamento, String motivo) {
        Matricula matricula = matriculas.porId(matriculaId)
                .orElseThrow(() -> new RegraDeNegocioException("Matricula nao encontrada"));
        Pagamento estornado = matricula.estornarPagamento(indiceDoPagamento, motivo);
        Matricula salva = matriculas.salvar(matricula);
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.PAGAMENTO_ESTORNADO, String.valueOf(salva.id()),
                "Estorno de " + estornado.valor() + " (" + estornado.formaDePagamento() + "): "
                        + estornado.motivoDoEstorno() + ". Total pago agora: " + salva.totalPago()));
        return Resumos.matricula(salva, nomes.de(salva.alunoId()));
    }
}

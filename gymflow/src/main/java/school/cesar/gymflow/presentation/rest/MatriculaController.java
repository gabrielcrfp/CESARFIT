package school.cesar.gymflow.presentation.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.application.usecase.AtualizarMatriculaUseCase;
import school.cesar.gymflow.application.usecase.CancelarMatriculaUseCase;
import school.cesar.gymflow.application.usecase.ExcluirMatriculaUseCase;
import school.cesar.gymflow.application.usecase.MatricularAlunoUseCase;
import school.cesar.gymflow.application.usecase.RegistrarPagamentoUseCase;
import school.cesar.gymflow.application.usecase.RenovarMatriculaUseCase;
import school.cesar.gymflow.application.usecase.ServicoDeConsultas;

/** Camada de apresentacao web (adaptador de entrada): CRUD de matriculas. */
@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatricularAlunoUseCase matricular;
    private final AtualizarMatriculaUseCase atualizar;
    private final ExcluirMatriculaUseCase excluir;
    private final RegistrarPagamentoUseCase registrarPagamento;
    private final RenovarMatriculaUseCase renovar;
    private final CancelarMatriculaUseCase cancelar;
    private final ServicoDeConsultas consultas;

    public MatriculaController(MatricularAlunoUseCase matricular,
                               AtualizarMatriculaUseCase atualizar,
                               ExcluirMatriculaUseCase excluir,
                               RegistrarPagamentoUseCase registrarPagamento,
                               RenovarMatriculaUseCase renovar,
                               CancelarMatriculaUseCase cancelar,
                               ServicoDeConsultas consultas) {
        this.matricular = matricular;
        this.atualizar = atualizar;
        this.excluir = excluir;
        this.registrarPagamento = registrarPagamento;
        this.renovar = renovar;
        this.cancelar = cancelar;
        this.consultas = consultas;
    }

    @PostMapping
    public ResponseEntity<Respostas.MatriculaResumo> matricular(@RequestBody Comandos.SalvarMatricula comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matricular.executar(comando));
    }

    @GetMapping
    public List<Respostas.MatriculaResumo> listar(@RequestParam(required = false) Long alunoId,
                                                  @RequestParam(required = false) String busca,
                                                  @RequestParam(required = false) String plano,
                                                  @RequestParam(required = false) String status) {
        return consultas.matriculas(alunoId, busca, plano, status);
    }

    @GetMapping("/{id}")
    public Respostas.MatriculaResumo detalhar(@PathVariable Long id) {
        return consultas.matricula(id);
    }

    @PutMapping("/{id}")
    public Respostas.MatriculaResumo atualizar(@PathVariable Long id,
                                               @RequestBody Comandos.SalvarMatricula comando) {
        return atualizar.executar(id, comando);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        excluir.executar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/pagamentos")
    public Respostas.MatriculaResumo pagar(@PathVariable Long id,
                                           @RequestBody Comandos.RegistrarPagamento corpo) {
        return registrarPagamento.executar(
                new Comandos.RegistrarPagamento(id, corpo.valor(), corpo.formaDePagamento()));
    }

    @PostMapping("/{id}/renovacoes")
    public Respostas.MatriculaResumo renovar(@PathVariable Long id) {
        return renovar.executar(id);
    }

    @PostMapping("/{id}/cancelamento")
    public Respostas.MatriculaResumo cancelar(@PathVariable Long id) {
        return cancelar.executar(id);
    }
}

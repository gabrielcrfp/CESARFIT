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
import school.cesar.gymflow.application.usecase.AtualizarAlunoUseCase;
import school.cesar.gymflow.application.usecase.CadastrarAlunoUseCase;
import school.cesar.gymflow.application.usecase.ExcluirAlunoUseCase;
import school.cesar.gymflow.application.usecase.ServicoDeConsultas;

/** Camada de apresentacao web (adaptador de entrada): CRUD de alunos. */
@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final CadastrarAlunoUseCase cadastrarAluno;
    private final AtualizarAlunoUseCase atualizarAluno;
    private final ExcluirAlunoUseCase excluirAluno;
    private final ServicoDeConsultas consultas;

    public AlunoController(CadastrarAlunoUseCase cadastrarAluno, AtualizarAlunoUseCase atualizarAluno,
                           ExcluirAlunoUseCase excluirAluno, ServicoDeConsultas consultas) {
        this.cadastrarAluno = cadastrarAluno;
        this.atualizarAluno = atualizarAluno;
        this.excluirAluno = excluirAluno;
        this.consultas = consultas;
    }

    @PostMapping
    public ResponseEntity<Respostas.AlunoResumo> cadastrar(@RequestBody Comandos.SalvarAluno comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cadastrarAluno.executar(comando));
    }

    @GetMapping
    public List<Respostas.AlunoResumo> listar(@RequestParam(required = false) String busca) {
        return consultas.alunos(busca);
    }

    @GetMapping("/{id}")
    public Respostas.AlunoDetalhe detalhar(@PathVariable Long id) {
        return consultas.aluno(id);
    }

    @PutMapping("/{id}")
    public Respostas.AlunoResumo atualizar(@PathVariable Long id, @RequestBody Comandos.SalvarAluno comando) {
        return atualizarAluno.executar(id, comando);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        excluirAluno.executar(id);
        return ResponseEntity.noContent().build();
    }
}

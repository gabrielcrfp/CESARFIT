package school.cesar.gymflow.presentation.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.application.usecase.ConcluirItemDeTreinoUseCase;
import school.cesar.gymflow.application.usecase.ConsultarProximoExercicioUseCase;
import school.cesar.gymflow.application.usecase.GerarFichaDeTreinoUseCase;
import school.cesar.gymflow.application.usecase.ServicoDeConsultas;

/** Camada de apresentacao web (adaptador de entrada). */
@RestController
@RequestMapping("/api/treinos")
public class TreinoController {

    private final GerarFichaDeTreinoUseCase gerarFicha;
    private final ConcluirItemDeTreinoUseCase concluirItem;
    private final ConsultarProximoExercicioUseCase proximoExercicio;
    private final ServicoDeConsultas consultas;

    public TreinoController(GerarFichaDeTreinoUseCase gerarFicha,
                            ConcluirItemDeTreinoUseCase concluirItem,
                            ConsultarProximoExercicioUseCase proximoExercicio,
                            ServicoDeConsultas consultas) {
        this.gerarFicha = gerarFicha;
        this.concluirItem = concluirItem;
        this.proximoExercicio = proximoExercicio;
        this.consultas = consultas;
    }

    @PostMapping("/fichas")
    public ResponseEntity<Respostas.FichaResumo> gerar(@RequestParam Long alunoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gerarFicha.executar(alunoId));
    }

    @GetMapping("/fichas")
    public List<Respostas.FichaResumo> listar(@RequestParam Long alunoId) {
        return consultas.fichasDoAluno(alunoId);
    }

    @PostMapping("/fichas/{id}/itens/{posicao}/conclusao")
    public Respostas.FichaResumo concluir(@PathVariable Long id, @PathVariable int posicao) {
        return concluirItem.executar(new Comandos.ConcluirItem(id, posicao));
    }

    @GetMapping("/fichas/{id}/proximo")
    public ResponseEntity<Respostas.ItemResumo> proximo(@PathVariable Long id) {
        return proximoExercicio.executar(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}

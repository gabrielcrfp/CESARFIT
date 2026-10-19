package school.cesar.gymflow.presentation.rest;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.application.usecase.CatalogoDeExerciciosUseCase;

/** Catalogo de exercicios (equipe). */
@RestController
@RequestMapping("/api/exercicios")
public class ExercicioController {

    private final CatalogoDeExerciciosUseCase catalogo;

    public ExercicioController(CatalogoDeExerciciosUseCase catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public List<Respostas.ExercicioResumo> listar() {
        return catalogo.listar();
    }

    @PostMapping
    public ResponseEntity<Respostas.ExercicioResumo> incluir(@RequestBody Comandos.SalvarExercicio comando) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogo.incluir(comando));
    }

    @PutMapping("/{id}")
    public Respostas.ExercicioResumo editar(@PathVariable Long id, @RequestBody Comandos.SalvarExercicio comando) {
        return catalogo.editar(id, comando);
    }

    @PostMapping("/{id}/desativacao")
    public Respostas.ExercicioResumo desativar(@PathVariable Long id) {
        return catalogo.desativar(id);
    }

    @PostMapping("/{id}/reativacao")
    public Respostas.ExercicioResumo reativar(@PathVariable Long id) {
        return catalogo.reativar(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        catalogo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

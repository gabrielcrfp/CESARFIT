package school.cesar.gymflow.infrastructure.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import school.cesar.gymflow.domain.treino.model.Exercicio;
import school.cesar.gymflow.domain.treino.model.GrupoMuscular;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeExercicios;

/** Catalogo padrao de exercicios (roda em todos os perfis, inclusive testes; inclui so os que ainda nao existem). */
@Component
@Order(1)
public class CatalogoInicial implements CommandLineRunner {

    private final RepositorioDeExercicios exercicios;

    public CatalogoInicial(RepositorioDeExercicios exercicios) {
        this.exercicios = exercicios;
    }

    @Override
    public void run(String... args) {
        List.of(
                Exercicio.novo("Supino reto", GrupoMuscular.PEITO, "Barra e banco"),
                Exercicio.novo("Supino inclinado com halteres", GrupoMuscular.PEITO, "Halteres e banco"),
                Exercicio.novo("Flexao de bracos", GrupoMuscular.PEITO, null),
                Exercicio.novo("Remada curvada", GrupoMuscular.COSTAS, "Barra"),
                Exercicio.novo("Remada baixa", GrupoMuscular.COSTAS, "Polia baixa"),
                Exercicio.novo("Puxada frontal", GrupoMuscular.COSTAS, "Polia alta"),
                Exercicio.novo("Agachamento livre", GrupoMuscular.PERNAS, "Barra e rack"),
                Exercicio.novo("Leg press", GrupoMuscular.PERNAS, "Leg press 45"),
                Exercicio.novo("Circuito de agachamento", GrupoMuscular.PERNAS, null),
                Exercicio.novo("Afundo com halteres", GrupoMuscular.PERNAS, "Halteres"),
                Exercicio.novo("Desenvolvimento militar", GrupoMuscular.OMBROS, "Barra"),
                Exercicio.novo("Elevacao lateral", GrupoMuscular.OMBROS, "Halteres"),
                Exercicio.novo("Rosca direta", GrupoMuscular.BRACOS, "Barra"),
                Exercicio.novo("Triceps na polia", GrupoMuscular.BRACOS, "Polia alta"),
                Exercicio.novo("Abdominal supra", GrupoMuscular.CORE, null),
                Exercicio.novo("Prancha abdominal", GrupoMuscular.CORE, null),
                Exercicio.novo("Esteira em ritmo moderado", GrupoMuscular.CARDIO, "Esteira"),
                Exercicio.novo("Bicicleta ergometrica", GrupoMuscular.CARDIO, "Bicicleta ergometrica"),
                Exercicio.novo("Polichinelo", GrupoMuscular.CARDIO, null),
                Exercicio.novo("Supino declinado", GrupoMuscular.PEITO, "Barra e banco"),
                Exercicio.novo("Peck deck", GrupoMuscular.PEITO, "Voador"),
                Exercicio.novo("Remada unilateral com halter", GrupoMuscular.COSTAS, "Halter e banco"),
                Exercicio.novo("Pulldown com triangulo", GrupoMuscular.COSTAS, "Polia alta"),
                Exercicio.novo("Levantamento terra", GrupoMuscular.COSTAS, "Barra"),
                Exercicio.novo("Cadeira extensora", GrupoMuscular.PERNAS, "Cadeira extensora"),
                Exercicio.novo("Mesa flexora", GrupoMuscular.PERNAS, "Mesa flexora"),
                Exercicio.novo("Stiff", GrupoMuscular.PERNAS, "Barra"),
                Exercicio.novo("Panturrilha em pe", GrupoMuscular.PERNAS, "Maquina de panturrilha"),
                Exercicio.novo("Elevacao frontal", GrupoMuscular.OMBROS, "Halteres"),
                Exercicio.novo("Desenvolvimento com halteres", GrupoMuscular.OMBROS, "Halteres e banco"),
                Exercicio.novo("Rosca martelo", GrupoMuscular.BRACOS, "Halteres"),
                Exercicio.novo("Triceps testa", GrupoMuscular.BRACOS, "Barra W"),
                Exercicio.novo("Rosca concentrada", GrupoMuscular.BRACOS, "Halter"),
                Exercicio.novo("Abdominal infra", GrupoMuscular.CORE, null),
                Exercicio.novo("Prancha lateral", GrupoMuscular.CORE, null),
                Exercicio.novo("Escada ergometrica", GrupoMuscular.CARDIO, "Escada ergometrica"),
                Exercicio.novo("Eliptico", GrupoMuscular.CARDIO, "Eliptico"),
                Exercicio.novo("Pular corda", GrupoMuscular.CARDIO, "Corda"))
                .stream()
                .filter(e -> exercicios.porNome(e.nome()).isEmpty())
                .forEach(exercicios::salvar);
    }
}

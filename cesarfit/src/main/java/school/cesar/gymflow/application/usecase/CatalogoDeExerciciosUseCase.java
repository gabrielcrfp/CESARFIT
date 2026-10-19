package school.cesar.gymflow.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import school.cesar.gymflow.application.dto.Comandos;
import school.cesar.gymflow.application.dto.Respostas;
import school.cesar.gymflow.domain.shared.EventoDeDominio;
import school.cesar.gymflow.domain.shared.PublicadorDeEventos;
import school.cesar.gymflow.domain.shared.RegraDeNegocioException;
import school.cesar.gymflow.domain.treino.model.Exercicio;
import school.cesar.gymflow.domain.treino.model.GrupoMuscular;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeExercicios;
import school.cesar.gymflow.domain.treino.repositorio.RepositorioDeFichas;

/**
 * Funcionalidade: gerenciamento do catalogo de exercicios.
 * Regras: nome unico; exercicio usado em fichas nao muda nome/grupo nem pode ser excluido (so desativado);
 * nao se desativa o ultimo exercicio ativo de um grupo, pois o gerador de fichas depende dele.
 */
@Service
public class CatalogoDeExerciciosUseCase {

    private final RepositorioDeExercicios exercicios;
    private final RepositorioDeFichas fichas;
    private final PublicadorDeEventos publicador;

    public CatalogoDeExerciciosUseCase(RepositorioDeExercicios exercicios, RepositorioDeFichas fichas,
                                       PublicadorDeEventos publicador) {
        this.exercicios = exercicios;
        this.fichas = fichas;
        this.publicador = publicador;
    }

    @Transactional(readOnly = true)
    public List<Respostas.ExercicioResumo> listar() {
        return exercicios.todos().stream().map(this::resumo).toList();
    }

    @Transactional
    public Respostas.ExercicioResumo incluir(Comandos.SalvarExercicio comando) {
        Exercicio novo = Exercicio.novo(comando.nome(), grupo(comando.grupoMuscular()), comando.equipamento());
        exigirNomeLivre(novo.nome(), null);
        Exercicio salvo = exercicios.salvar(novo);
        publicar(salvo, "incluido no catalogo");
        return resumo(salvo);
    }

    @Transactional
    public Respostas.ExercicioResumo editar(Long id, Comandos.SalvarExercicio comando) {
        Exercicio exercicio = buscar(id);
        GrupoMuscular novoGrupo = grupo(comando.grupoMuscular());
        if (exercicio.ativo() && exercicio.grupoMuscular() != novoGrupo) {
            exigirOutroAtivoNoGrupo(exercicio);
        }
        exercicio.alterar(comando.nome(), novoGrupo, comando.equipamento(), fichas.usaExercicio(exercicio.nome()));
        exigirNomeLivre(exercicio.nome(), id);
        Exercicio salvo = exercicios.salvar(exercicio);
        publicar(salvo, "alterado");
        return resumo(salvo);
    }

    @Transactional
    public Respostas.ExercicioResumo desativar(Long id) {
        Exercicio exercicio = buscar(id);
        exigirOutroAtivoNoGrupo(exercicio);
        exercicio.desativar();
        Exercicio salvo = exercicios.salvar(exercicio);
        publicar(salvo, "desativado");
        return resumo(salvo);
    }

    @Transactional
    public Respostas.ExercicioResumo reativar(Long id) {
        Exercicio exercicio = buscar(id);
        exercicio.reativar();
        Exercicio salvo = exercicios.salvar(exercicio);
        publicar(salvo, "reativado");
        return resumo(salvo);
    }

    @Transactional
    public void excluir(Long id) {
        Exercicio exercicio = buscar(id);
        if (fichas.usaExercicio(exercicio.nome())) {
            throw new RegraDeNegocioException("Exercicio usado em fichas nao pode ser excluido; desative-o");
        }
        if (exercicio.ativo()) {
            exigirOutroAtivoNoGrupo(exercicio);
        }
        exercicios.excluir(id);
        publicar(exercicio, "excluido do catalogo");
    }

    private void exigirOutroAtivoNoGrupo(Exercicio exercicio) {
        boolean haOutro = exercicios.ativos().stream()
                .anyMatch(e -> e.grupoMuscular() == exercicio.grupoMuscular() && !e.id().equals(exercicio.id()));
        if (exercicio.ativo() && !haOutro) {
            throw new RegraDeNegocioException("Nao e possivel retirar o ultimo exercicio ativo do grupo "
                    + exercicio.grupoMuscular() + ": a geracao de fichas depende dele");
        }
    }

    private void exigirNomeLivre(String nome, Long idAtual) {
        exercicios.porNome(nome).filter(e -> !e.id().equals(idAtual)).ifPresent(e -> {
            throw new RegraDeNegocioException("Ja existe um exercicio com o nome " + e.nome());
        });
    }

    private Exercicio buscar(Long id) {
        return exercicios.porId(id).orElseThrow(() -> new RegraDeNegocioException("Exercicio nao encontrado"));
    }

    static GrupoMuscular grupo(String texto) {
        try {
            return GrupoMuscular.valueOf(texto == null ? "" : texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegraDeNegocioException("Grupo muscular invalido: " + texto);
        }
    }

    private void publicar(Exercicio e, String acao) {
        publicador.publicar(EventoDeDominio.de(EventoDeDominio.CATALOGO_ALTERADO,
                String.valueOf(e.id()), "Exercicio " + e.nome() + " " + acao));
    }

    Respostas.ExercicioResumo resumo(Exercicio e) {
        return new Respostas.ExercicioResumo(e.id(), e.nome(), e.grupoMuscular().name(), e.equipamento(),
                e.ativo(), fichas.usaExercicio(e.nome()));
    }
}

package school.cesar.gymflow.domain.treino.model;

/** Entidade do subdominio de Treino. */
public record Exercicio(Long id, String nome, GrupoMuscular grupoMuscular, boolean exigeEquipamento) {
}
